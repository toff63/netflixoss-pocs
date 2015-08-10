package com.github.diegopacheco.sandbox.java.netflixoss.karyon.poc.common;

import com.netflix.client.http.HttpResponse;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import netflix.karyon.transport.interceptor.OutboundInterceptor;
import rx.Observable;

public class HeadersOutInterceptor implements OutboundInterceptor<HttpServerResponse<ByteBuf>>{

    @Override
    public Observable<Void> out(HttpServerResponse<ByteBuf> response) {
        response.getHeaders().add("MY-HEADER", "This is a header added dynamically");
        return null;
    }
}
