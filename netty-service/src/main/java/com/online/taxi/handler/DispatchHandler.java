package com.online.taxi.handler;

import com.google.protobuf.Message;
import com.online.taxi.consts.Const;
import com.online.taxi.context.ServerContext;
import com.online.taxi.proto.MessageProto;
import com.online.taxi.user.User;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
@Slf4j
public class DispatchHandler<P extends User> extends SimpleChannelInboundHandler<Message> {
    @Autowired
    private ServerContext serverContext;

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        serverContext.removeUser(ctx.channel());
    }

    /**
     * 发生异常，从 serverContext.users 中移除
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        serverContext.removeUser(ctx.channel());
    }

    /**
     * 当客户端连接服务端之后（打开连接）
     * 获取客户端的 channel，并且放到 serverContext.users 中去进行管理
     * 覆盖handlerAdded()方法，可以省去“super.channelRegistered(ctx);”这一段代码
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        log.info("channel registered :{}", ctx.channel());
        String id = serverContext.getNewId() + "";
        ctx.channel().attr(Const.playerKey).set(id);
        User user = new User();
        user.setChannel(ctx.channel());
        user.setId(id);
        serverContext.addUser(user);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        User user = null;
        MessageProto.RequestProto requestProto = MessageProto.RequestProto.parseFrom(message.toByteArray());
        log.info("message = " + message);
        // String playerId = channelHandlerContext.channel().attr(Const.playerKey).get();
        // if (playerId == null) {
        //     user = new User();
        //
        // }
        // user.setChannel(channelHandlerContext.channel());

    }
}
