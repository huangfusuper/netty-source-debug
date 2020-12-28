package com.books.netty_in_action.case_12.websocket.handler;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;

/**
 * http消息处理器
 *
 * @author huangfu
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String wsUri;
    private static final File INDEX;

    static {
        URL location = HttpRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            String path = location.toURI() + "index.html";
            path = !path.contains("file:") ? path : path.substring(5);
            INDEX = new File(path);
        } catch (Exception e) {
            throw new IllegalStateException("没有这个index.html");
        }
    }

    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        //对比，当前的url是websocket的还是http的，如果以 wsUri 结尾即为websocket， 应当调用下一个处理器
        if (wsUri.equalsIgnoreCase(request.uri())) {
            //调用下一个处理器的 channelRead0 并将 request的引用计数+1 因为 SimpleChannelInboundHandler 会自动释放引用
            ctx.fireChannelRead(request.retain());
        } else {
            //神兵小将
            //当且仅当指定消息包含Expect报头且唯一存在的期望是100连续期望时，才返回true 。
            // 请注意，如果期望标头对于消息无效（例如，消息是响应，或者消息上的版本是HTTP / 1.0），则此方法返回false 。
            if (HttpUtil.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }
            //读取文件地址
            RandomAccessFile file = new RandomAccessFile(INDEX, "r");
            HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
            //设置响应内容类型
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
            //当且仅当连接可以保持打开状态并因此“保持活动”时，才返回true 。
            // 这种方法尊重价值。 "Connection"标头，然后是HttpVersion.isKeepAliveDefault()的返回值
            boolean keepAlive = HttpUtil.isKeepAlive(request);
            //是否保持连接
            if (keepAlive) {
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            ctx.write(response);


            //将index.html写到客户端
            if (ctx.pipeline().get(SslHandler.class) == null) {
                //不需要加密传输
                ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
            } else {
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }
            //刷新缓冲区到客户端
            ChannelFuture channelFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

            //如果不是长连接方式的  就关闭通道
            if (!keepAlive) {
                channelFuture.addListener(ChannelFutureListener.CLOSE);
            }


        }
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE
        );
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
