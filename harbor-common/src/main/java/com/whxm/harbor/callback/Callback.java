package com.whxm.harbor.callback;

/**
 * 本来是javax.util.Callback
 * 但是这个依赖是来自jdk1.8的,放到其他环境不一定能跑
 * 只能自己重写写一个了
 *
 * @param <P>
 * @param <R>
 */
public interface Callback<P, R> {

    R call(P param);
}
