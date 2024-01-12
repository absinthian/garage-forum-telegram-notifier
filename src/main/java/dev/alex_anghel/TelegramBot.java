package dev.alex_anghel;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class TelegramBot {

    Logger LOG = LoggerFactory.getLogger(TelegramBot.class);

    @ConfigProperty(name = "telegram.token")
    String token;

    @ConfigProperty(name = "telegram.chatId")
    String chatId;

    private Client client;
    private WebTarget baseTarget;

    @PostConstruct
    void initClient() {
        client = ClientBuilder.newClient();
        baseTarget = client.target("https://api.telegram.org/bot{token}")
                .resolveTemplate("token", this.token);
    }

    public void sendMessage(String message) {
        try {
            Response response = baseTarget.path("sendMessage")
                    .queryParam("chat_id", chatId)
                    .queryParam("text", message)
                    .request()
                    .get();

            JsonObject json = response.readEntity(JsonObject.class);
            boolean ok = json.getBoolean("ok", false);
            if (!ok) {
                LOG.warn("Couldn't successfully send message");
            }
        } catch (Exception e) {
            LOG.error("Couldn't successfully send message, ", e);
        }
    }

    @PreDestroy
    private void closeClient() {
        client.close();
    }

}
