package dk.something.mj;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.MessageHelper;

import javax.jms.ConnectionFactory;
import java.util.Date;

public class SendObjectToActiveMQ
{
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
                //from("direct:start")
                //        .to("jms:queue:qb"); // Change from to --> from for changing the direction
                /*
                How do we set it up so we can consume??
                from("jms:queue:qb")
                        .to("direct:start");
                */
                from("direct:start")
                        .setExchangePattern(ExchangePattern.InOnly)
                        .convertBodyTo(String.class)
                        .process(e -> log.info("Response : "+MessageHelper.extractBodyAsString(e.getIn())))
                        .to("jms:queue:qb");
                /*from("direct:start")
                        .routeId("urlRoute")
                        .setExchangePattern(ExchangePattern.InOut)
                        .convertBodyTo(String.class)
                        .recipientList(simple("http://http://127.0.0.1:8000/pizza/placeOrder/"))
                        .process(e -> log.info("Response : "+MessageHelper.extractBodyAsString(e.getIn())))
                        .to("jms:queue:qb");*/
                /*from("direct:start")
                        .setHeader("CamelHttpMethod", constant("GET"))
                        .process(e -> log.info("Response : "+MessageHelper.extractBodyAsString(e.getIn())))
                        .to("http4://127.0.0.1:8000/pizza/placeOrder/");*/
            }
        });

        context.start();
        long long2 = 500;

        ProducerTemplate producerTemplate = context.createProducerTemplate();
        ConsumerTemplate consumerTemplate = context.createConsumerTemplate();
        producerTemplate.sendBody("direct:start", new Date());
        //producerTemplate.send("direct:start", new Date(), 0, 0, long2);
       /* producerTemplate.sendBody("direct:start", "CAN YOU READ THIS?");*/
        Message string = consumerTemplate.receive("jms:queue:qb").getIn();
        System.out.println(
                string.getBody().toString()
        );

        //producerTemplate.request("jms:queue:qb", );
        // System.out.println(producerTemplate.requestBody("jms:queue:qb", "Tue"));

    }

}
