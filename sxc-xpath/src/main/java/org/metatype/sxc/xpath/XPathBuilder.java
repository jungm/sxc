package org.metatype.sxc.xpath;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPrimitiveType;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import org.jaxen.JaxenHandler;
import org.jaxen.expr.AllNodeStep;
import org.jaxen.expr.EqualityExpr;
import org.jaxen.expr.Expr;
import org.jaxen.expr.FunctionCallExpr;
import org.jaxen.expr.LiteralExpr;
import org.jaxen.expr.LocationPath;
import org.jaxen.expr.LogicalExpr;
import org.jaxen.expr.NameStep;
import org.jaxen.expr.NumberExpr;
import org.jaxen.expr.Predicate;
import org.jaxen.expr.TextNodeStep;
import org.jaxen.expr.XPathExpr;
import org.jaxen.saxpath.Axis;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.saxpath.helpers.XPathReaderFactory;
import org.metatype.sxc.Context;
import org.metatype.sxc.builder.Builder;
import org.metatype.sxc.builder.CodeBody;
import org.metatype.sxc.builder.ElementParserBuilder;
import org.metatype.sxc.builder.ParserBuilder;
import org.metatype.sxc.builder.impl.BuilderImpl;
import org.metatype.sxc.xpath.impl.XPathEvaluatorImpl;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class XPathBuilder {
    private Map<String,String> namespaceContext;
    private ElementParserBuilder parserBldr;
    
    private Map<String, XPathEventHandler> eventHandlers = new HashMap<String, XPathEventHandler>();
    private Map<String, Object> vars = new HashMap<String, Object>();
    private JType eventHandlerType;
    private JType stringType;
    private Builder builder;
    private JType eventType;
    private int varCount = 0;
    private int elementCounters = 0;
    private JPrimitiveType boolType;
    private JPrimitiveType intType;
    private JCodeModel model;

    public XPathBuilder() {
        super();
        
        builder = new BuilderImpl();
        parserBldr = builder.getParserBuilder();
        
        model = parserBldr.getCodeModel();
        eventHandlerType = model._ref(XPathEventHandler.class);
        eventType = model._ref(XPathEvent.class);
        stringType = model._ref(String.class);
        boolType = model.BOOLEAN;
        intType = model.INT;
    }

    public void listen(String expr, XPathEventHandler handler) {
        eventHandlers.put(expr, handler);
    }
    
    public XPathEvaluator compile() {
        for (Map.Entry<String, XPathEventHandler> e : eventHandlers.entrySet()) {
            compileEventHandler(e.getKey(), e.getValue());
        }
        
        Context context = builder.compile();
        context.putAll(vars);
        
        return new XPathEvaluatorImpl(context);
    }
    
    public void compileEventHandler(String expr, XPathEventHandler eventHandler) {
        String varName = "obj" + vars.size();
        vars.put(varName, eventHandler);
        
        ParserBuilder xpathBuilder = parserBldr;
        JBlock block;
        try {
            org.jaxen.saxpath.XPathReader reader = XPathReaderFactory.createReader();
            
            JaxenHandler handler = new JaxenHandler();
            reader.setXPathHandler(handler);
            reader.parse(expr);
            
            XPathExpr path = handler.getXPathExpr(true);
            
            Object o = handleExpression(parserBldr, path.getRootExpr());
            if (o instanceof ExpressionState) {
                ExpressionState exp = (ExpressionState) o;
                JVar var = exp.getVar();
                ParserBuilder builder = exp.getBuilder();
                block = builder.getBody().getBlock();
                
                block = block._if(var)._then();
            } else {
                xpathBuilder = (ParserBuilder) o;
                block = xpathBuilder.getBody().getBlock();
            }
        } catch (SAXPathException e) {
            throw new XPathException(e);
        }
        
        CodeBody body = xpathBuilder.getBody();
        
        // grab the event handler out of the context
        JVar handlerVar = block.decl(eventHandlerType, varName, 
                                    JExpr.cast(eventHandlerType, 
                                               JExpr._super().ref("context").invoke("get").arg(varName)));

        block.add(handlerVar.invoke("onMatch").arg(JExpr._new(eventType).arg(JExpr.lit(expr)).arg(xpathBuilder.getXSR())));
    }

    private Object handleExpression(ElementParserBuilder xpathBuilder, Expr expr) {
        // System.out.println("Expression " + expr);
        if (expr instanceof LocationPath) {
            return handle(xpathBuilder, (LocationPath) expr);
        } else if (expr instanceof EqualityExpr) {
            return handle(xpathBuilder, (EqualityExpr) expr);
        } else if (expr instanceof LiteralExpr) {
            return handle(xpathBuilder, (LiteralExpr) expr);
        } else if (expr instanceof FunctionCallExpr) {
            return handle(xpathBuilder, (FunctionCallExpr) expr);
        } else if (expr instanceof LogicalExpr) {
            return handle(xpathBuilder, (LogicalExpr) expr);
        } else if (expr instanceof NumberExpr) {
            return handle(xpathBuilder, (NumberExpr) expr);
        } else {
            throw new XPathException("Unknown expression type " + expr);
        }
    }

    private ExpressionState handle(ElementParserBuilder xpathBuilder, LiteralExpr expr) {
        JVar var = xpathBuilder.getBody().decl(stringType, 
                                               "_literal" + varCount++,
                                               JExpr.lit(expr.getLiteral()));
        return new ExpressionState(xpathBuilder, var);
    }
    
    private ParserBuilder handle(ElementParserBuilder parent, LogicalExpr expr) {
        Object left = handleExpression(parent, expr.getLHS());
        Object right = handleExpression(parent, expr.getRHS());
        
        JBlock block = parent.getBody().getBlock();
        
        JVar b1 = ((ExpressionState) left).getVar();
        JVar b2 = ((ExpressionState) right).getVar();
        
        String op = expr.getOperator();
        
        JBlock newBlock;
        if (op.equals("and")) {
            newBlock = block._if(b1.cand(b2))._then();
        } else if (op.equals("or")) {
            newBlock = block._if(b1.cor(b2))._then();
        } else {
            throw new UnsupportedOperationException("Operator " + op + " is not supported");
        }
        
        return parent.newState(newBlock);
    }
    
    private ExpressionState handle(ElementParserBuilder xpathBuilder, FunctionCallExpr expr) {

        String name = "functValue" + varCount++;
        String functionName = expr.getFunctionName();
        JVar var;
        // See http://www.w3schools.com/xpath/xpath_functions.asp for a complete list
        if ("local-name".equals(functionName)) {
            var =  xpathBuilder.getBody().decl(stringType, 
                                               name, 
                                               xpathBuilder.getXSR().invoke("getLocalName"));
        } else if ("namespace-uri".equals(functionName)) {
            var =  xpathBuilder.getBody().decl(stringType, 
                                               name, 
                                               xpathBuilder.getXSR().invoke("getNamespaceURI"));
        } else {
            throw new XPathException("Function " + functionName + " is not understood!");
        }
        
        return new ExpressionState(xpathBuilder, var);
    }

    private ExpressionState handle(ElementParserBuilder parent, EqualityExpr expr) {
        Object leftObj = handleExpression(parent, expr.getLHS());
        ExpressionState right = (ExpressionState) handleExpression(parent, expr.getRHS());
        
        JVar leftVar;
        JVar rightVar;
        ParserBuilder parent2; 
        if (leftObj instanceof ParserBuilder) {
            // We've got an attribute.. i.e. [@foo='bar']
            ParserBuilder attBuilder = (ParserBuilder) leftObj;
            leftVar = attBuilder.as(String.class);
            rightVar = attBuilder.passParentVariable(right.getVar());
            parent2 = attBuilder;
        } else {
            leftVar = ((ExpressionState) leftObj).getVar();
            rightVar = right.getVar();
            parent2 = parent;
        }
        
        JVar var = parent2.getBody().decl(boolType,
                                          "b" + varCount++,
                                          leftVar.invoke("equals").arg(rightVar));
        
        return new ExpressionState(parent2, var);
    }

    private Object handle(ElementParserBuilder xpathBuilder, LocationPath path) {
        Object returnObj = xpathBuilder;
        
        // look for the next part on all child elements
        boolean globalElement = false;
        for (Iterator itr = path.getSteps().iterator(); itr.hasNext();) {
            Object o = itr.next();
            
            if (o instanceof NameStep) {
                returnObj = handleNameStep((ParserBuilder) returnObj, (NameStep) o, globalElement);
                globalElement = false;
            } else if (o instanceof AllNodeStep) {
                globalElement = true;
            } else if (o instanceof TextNodeStep) {
                returnObj = handleTextNodeStep((ParserBuilder) returnObj, (TextNodeStep) o);
            } else {
                throw new XPathException("Unsupported expression: " + o);
            }
        }
        
        return returnObj;
    }

    private ExpressionState handleTextNodeStep(ParserBuilder returnBuilder, TextNodeStep step) {
        JVar var = returnBuilder.as(String.class);
        return new ExpressionState(returnBuilder, var);
    }

    private Object handleNameStep(ParserBuilder returnBuilder, NameStep step, boolean globalElement) {
        String prefix = step.getPrefix();
        String ns = "";
        if (prefix != null && !prefix.equals("")) {
            ns = namespaceContext.get(prefix);
            
            if (ns == null) {
                throw new XPathException("Could not find namespace for prefix: " + prefix);
            }
        }
        
        QName n = new QName(ns, step.getLocalName());
        
        ElementParserBuilder elBuilder = ((ElementParserBuilder) returnBuilder);
        if (step.getAxis() == Axis.CHILD) {
            if (n.getLocalPart().equals("*")) {
                returnBuilder = elBuilder.expectAnyElement();
            } else if (globalElement) {
                returnBuilder = elBuilder.expectGlobalElement(n);
            } else {
                returnBuilder = elBuilder.expectElement(n);
            }
        } else if (step.getAxis() == Axis.ATTRIBUTE) {
            returnBuilder = elBuilder.expectAttribute(n);
        } else {
            throw new XPathException("Unsupported axis: " + step.getAxis());
        }
        
        return handlePredicates(returnBuilder, step.getPredicateSet().getPredicates());
    }

    private Object handle(ElementParserBuilder xpathBuilder, NumberExpr expr) {
//        xpathBuilder = xpathBuilder.newState();
        
	JBlock block = xpathBuilder.getBody().getBlock();
	
        JVar counterVar = parserBldr.getBody().field(JMod.PUBLIC, intType, "counter" + elementCounters++, JExpr.lit(0));
        
        block.assignPlus(counterVar, JExpr.lit(1));
		
	JBlock then = block._if(counterVar.eq(JExpr.lit((int) Double.valueOf(expr.getText()).doubleValue())))._then();
	
	return xpathBuilder.newState(then);
    }

    private Object handlePredicates(ParserBuilder returnBuilder, List<?> predicates) {
        Object returnObj = returnBuilder;
        for (Iterator<?> pitr = predicates.iterator(); pitr.hasNext();) {
            Predicate p = (Predicate) pitr.next();
            
            returnObj = 
                handleExpression((ElementParserBuilder) returnObj, p.getExpr());
        }
        return returnObj;
    }

    public Map<String, String> getNamespaceContext() {
        return namespaceContext;
    }    

    public void setNamespaceContext(Map<String, String> namespaceContext) {
        this.namespaceContext = namespaceContext;
    }

    public void addPrefix(String prefix, String namespace) {
        if (namespaceContext == null) {
            namespaceContext = new HashMap<String, String>();
        }
        namespaceContext.put(prefix, namespace);
    }
    
    public void addAllPrefixes(Map<String, String> prefixes) {
        if (namespaceContext == null) {
            namespaceContext = new HashMap<String, String>();
        }
        namespaceContext.putAll(prefixes);
    }
    
    public static class ExpressionState {
        private JVar var;
        private ParserBuilder builder;

        public ExpressionState(ParserBuilder builder, JVar var) {
            this.builder = builder;
            this.var = var;
        }

        public ParserBuilder getBuilder() {
            return builder;
        }

        public void setBuilder(ParserBuilder builder) {
            this.builder = builder;
        }

        public JVar getVar() {
            return var;
        }

        public void setVar(JVar var) {
            this.var = var;
        }
        
    }
}
