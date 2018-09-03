package fun.pplm.msc.websocket;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;

import java.util.HashMap;
import java.util.Map;

public class WebSocketVerticle extends AbstractVerticle {
    // ����ÿһ�����ӵ���������ͨ��
    private Map<String, ServerWebSocket> connectionMap = new HashMap<>(16);

    @Override
    public void start() throws Exception {

        HttpServer server = vertx.createHttpServer();
/*
        Router router = Router.router(vertx);

        router.route("/").handler(routingContext -> {
            routingContext.response().sendFile("html/ws.html");
        
        });
        */
        websocketMethod(server);
        server.listen(8080, ar -> {
        	System.out.println("Server started on port "+ ar.result().actualPort());
        });
      //  server.requestHandler(router::accept).listen(8080);
    }

    public void websocketMethod(HttpServer server) {
        server.websocketHandler(webSocket -> {
            // ��ȡÿһ�����ӵ�ID
            String id = webSocket.binaryHandlerID();
            // �жϵ�ǰ���ӵ�ID�Ƿ������map�����У��������������ӽ�map������
            if (!checkID(id)) {
                connectionMap.put(id, webSocket);
            }

                        //��WebSocket ����
            webSocket.frameHandler(handler -> {
                String textData = handler.textData();
                String currID = webSocket.binaryHandlerID();
                //���ǵ�ǰ���ӵ���������ÿһ��WebSocket���ӷ�����Ϣ
                for (Map.Entry<String, ServerWebSocket> entry : connectionMap.entrySet()) {
                    /* �����ı���Ϣ
                    �ı���Ϣ�ƺ���֧��ͼƬ�ȶ�������Ϣ
                    ��Ҫ���Ͷ�������Ϣ������writeBinaryMessage����
                    */
                    entry.getValue().writeTextMessage(textData);
                }
            });

            // �ͻ���WebSocket �ر�ʱ������ǰID��map���Ƴ�
            webSocket.closeHandler(handler -> connectionMap.remove(id) );
        });
    }
    // ��鵱ǰID�Ƿ��Ѿ�������map��
    public boolean checkID(String id) {
        return connectionMap.containsKey(id);
    }
}