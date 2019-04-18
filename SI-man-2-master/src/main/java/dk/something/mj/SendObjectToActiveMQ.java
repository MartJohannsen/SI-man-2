package dk.something.mj;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

import javax.jms.ConnectionFactory;
import javax.jms.TextMessage;
import java.util.Date;

public class SendObjectToActiveMQ
{
    /*
    *
    *  THIS ONE WORKS, AND CAN PUSH MESSAGES TO THE QUEUE
    *
    * */
    public static void main(String[] args) throws Exception
    {
        CamelContext context = new DefaultCamelContext();

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        context.addRoutes(new RouteBuilder()
        {
            @Override
            public void configure() throws Exception
            {
                from("direct:start")
                        .to("jms:queue:Request");
            }
        });

        context.start();

        ProducerTemplate producerTemplate = context.createProducerTemplate();
       // ConsumerTemplate consumerTemplate = context.createConsumerTemplate();
        producerTemplate.sendBody("direct:start", "CAN YOU READ THIS?");
       // Message string = consumerTemplate.receive("jms:queue:Request").getIn();
       // System.out.println(string.getBody().toString());

        //producerTemplate.request("jms:queue:qb", );
        // System.out.println(producerTemplate.requestBody("jms:queue:qb", "Tue"));

    }

}
