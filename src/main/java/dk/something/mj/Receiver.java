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
import java.util.ArrayList;

public class Receiver
{
    private static ArrayList<String> userList = new ArrayList<>();

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
                from("direct:starts")
                        .to("jms:queue:Reply");
                from("direct:invalid")
                        .to("jms:queue:Invalid");
            }
        });

        context.start();

        ConsumerTemplate consumerTemplate = context.createConsumerTemplate();

        Message string = consumerTemplate.receive("jms:queue:Request").getIn();
        System.out.println("From request queue: " + string.getBody().toString());

        boolean valid = interpretCommand(string.getBody().toString());

        ProducerTemplate producerTemplate = context.createProducerTemplate();

        if(valid)
        {
            System.out.println("To reply queue: " + userList.toString() + " REPLY");

            producerTemplate.sendBody("direct:starts", userList.toString());
        }
        else
        {
            producerTemplate.sendBody("direct:invalid", "Command is Invalid");
        }
    }

    public static boolean interpretCommand(String message)
    {
        String [] arrOfStr = message.split(",");
        String command = arrOfStr[0];
        String userName = arrOfStr[1];

        if (command.equalsIgnoreCase("createuser"))
        {
            createUser(userName);
            return true;
        }
        else if (command.equalsIgnoreCase("deleteuser"))
        {
            deleteUser(userName);
            return true;
        }
        else
        {
            return false;
        }
    }

    public static void createUser(String userName)
    {
        userList.add(userName);
    }

    public static void deleteUser(String userName)
    {
        userList.remove(userName);
    }
}
