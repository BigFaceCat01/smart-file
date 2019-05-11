package com.hxb.smart.rpcv2.core.net.impl.netty.server;

import com.hxb.smart.rpcv2.core.net.AbstractServer;
import com.hxb.smart.rpcv2.core.net.impl.netty.protocol.impl.NettyDecode;
import com.hxb.smart.rpcv2.core.net.impl.netty.protocol.impl.NettyEncode;
import com.hxb.smart.rpcv2.core.net.param.RpcRequest;
import com.hxb.smart.rpcv2.serializer.AbstractSerializer;
import com.hxb.smart.rpcv2.util.IpUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

/**
 * @author Created by huang xiao bao
 * @date 2019-05-11 11:22:25
 */
public class NettyServer extends AbstractServer {
    private EventLoopGroup workGroup;
    private EventLoopGroup bossGroup;
    private Channel channel;

    @Override
    public void init(String address, AbstractSerializer serializer) throws Exception {
        Object[] ipPort = IpUtil.parseIpPort(address);
        String host = (String) ipPort[0];
        int port = (int) ipPort[1];

        ServerBootstrap server = new ServerBootstrap();

        this.workGroup = new NioEventLoopGroup();
        this.bossGroup = new NioEventLoopGroup();
        server.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new NettyDecode(RpcRequest.class, serializer))
                                .addLast(new NettyEncode(serializer))
                                .addLast(new NettyServerHandler());
                    }
                });
        this.channel = server.bind(new InetSocketAddress(host, port))
                .sync()
                .channel();
    }
}
