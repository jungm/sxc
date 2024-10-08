package org.metatype.sxc.builder;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JType;
import org.metatype.sxc.Context;
import org.metatype.sxc.Writer;
import org.metatype.sxc.builder.impl.BuilderImpl;
import org.metatype.sxc.util.XoTestCase;
import org.w3c.dom.Document;

import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.io.File;

public class WriterBuilderTest extends XoTestCase {

    public void testBuilder() throws Exception {
        System.setProperty("org.metatype.sxc.output.directory", "target/tmp-jaxb");
        Builder builder = new BuilderImpl();
        JCodeModel model = builder.getCodeModel();
        JType cusClass = model._ref(Customer.class);
        JType stringType = model._ref(String.class);
        JType intType = model._ref(int.class);
        
        ElementWriterBuilder b = builder.getWriterBuilder();
        
        ElementWriterBuilder custBldr = 
            b.writeElement(new QName("customer"), 
                           b.getObject()._instanceof(cusClass), 
                           cusClass, 
                           b.getObject());
        
        ElementWriterBuilder idBldr = custBldr.writeElement(new QName("id"), 
                                                            intType, 
                                                            custBldr.getObject().invoke("getId"));
        idBldr.writeAsInt();
        
        WriterBuilder attBuilder = custBldr.writeAttribute(new QName("attValue"), 
                                                           stringType,
                                                           custBldr.getObject().invoke("getAttribute"));
        attBuilder.writeAs(String.class);
        
        ElementWriterBuilder nameBldr = custBldr.writeElement(new QName("name"), stringType, custBldr.getObject().invoke("getName"));
        nameBldr.writeAsString();
        
        File file = new File("target/tmp-jaxb");
        
        builder.write(file);
        Context context = builder.compile();
        
        Writer writer = context.createWriter();
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        Customer customer = new Customer();
        customer.setName("Dan Diephouse");
        customer.setId(123);
        customer.setAttribute("value");
        
        writer.write(bo, customer);
        bo.close();
        
        Document document = readDocument(bo.toByteArray());
        
        assertValid("/customer[@attValue='value']", document);
        assertValid("/customer/id[text()='123']", document);
    }
}
