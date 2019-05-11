package com.hxb.smart.rpcv2.core.net.impl.netty.protocol.impl;

import com.hxb.smart.rpcv2.serializer.AbstractSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 这里传输协议使用 length + data 协议
 * @author Created by huang xiao bao
 * @date 2019-05-07 09:10:04
 */
public class NettyDecode extends ByteToMessageDecoder {
    /**
     * 整数占用字节数
     */
    public static final int INT_BYTE = 4;

    private AbstractSerializer serializer;
    private Class<?> target;

    public NettyDecode(Class<?> target, AbstractSerializer serializer){
        this.target = target;
        this.serializer = serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        /**
         * 读取数据长度
         */
        if(in.readableBytes() < INT_BYTE){
            //若长度不足整数字节，不做操作
        }
        //标记当前位置
        in.markReaderIndex();
        //读取数据长度
        int len = in.readInt();
        //若可读数据长度小于len
        if(in.readableBytes() < len){
            //则将读索引置为读取len之前的位置
            in.resetReaderIndex();
        }
        //读取数据
        byte[] data = new byte[len];
        in.readBytes(data);
        //序列化
        Object result = serializer.deserialize(data, target);
        out.add(result);
        ctx.flush();
    }
}
