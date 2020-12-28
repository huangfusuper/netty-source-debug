package com.books.netty_in_action.case_12.websocket;

import com.books.netty_in_action.case_12.websocket.handler.HttpRequestHandler;
import com.books.netty_in_action.case_12.websocket.handler.TextWebSocketFrameHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author huangfu
 */
public class ChatServerInitializer extends ChannelInitializer<SocketChannel> {

    private final ChannelGroup channelGroup;

    public ChatServerInitializer(ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpObjectAggregator(64*1024));
        pipeline.addLast(new HttpRequestHandler("/huangfu"));
        pipeline.addLast(new WebSocketServerProtocolHandler("/huangfu"));
        pipeline.addLast(new TextWebSocketFrameHandler(channelGroup));

    }
}
