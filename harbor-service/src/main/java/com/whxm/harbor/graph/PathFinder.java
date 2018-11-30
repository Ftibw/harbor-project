package com.whxm.harbor.graph;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @param <ID> 顶点ID
 * @param <V>  顶点对象:vertex
 * @param <E>  边对象:edge
 * @author : Ftibw
 * @date : 2018/11/29 13:44
 */
public class PathFinder<ID, V, E, W extends Weight<W>> {
    private static final String BEELINE_POINT = "path";
    private static final String BEELINE_WEIGHT = "weight";
    /**
     * 顶点集合
     */
    private Map<ID, V> vertices;
    /**
     * 邻接边表
     */
    private Map<ID, List<E>> adjacencyTable;

//  private ID startId;
    /**
     * 终点
     */
    private ID endId;
    /**
     * 当前点到终点的最短路线边权值总和估值的获取算法,由外部提供
     */
    private BiFunction<V, V, W> getterOfH;
    /**
     * 包装点表
     * 作用---避免 loop test if point in openList
     */
    private Map<ID, Wrapper> wrapperTable = new HashMap<>();
    /**
     * 待检索点集合
     */
    private PriorityQueue<Wrapper> openList = new PriorityQueue<>((wrapper1, wrapper2) -> wrapper1.f.compareTo(wrapper2.f));

    private final Wrapper prototype = new Wrapper();

    private class Wrapper implements Cloneable {
        /**
         * 图中的顶点
         */
        ID pointId;
        /**
         * 起点到终点最短路径边权值总和的估算值
         */
        W f;
        /**
         * 起点到当前点已确认的最短路径边权值之和
         */
        W g;
        /**
         * 当前点到终点的最短路线边权值总和估值
         */
        W h;
        /**
         * 当前点是否已经检索过
         */
        Boolean closed;
        /**
         * 当前点的前继
         */
        Wrapper previous;

        public Object clone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                throw new RuntimeException("PathFinder#Wrapper clone failed");
            }
        }

    }

    /**
     * 构造寻路器
     *
     * @param vertices       路径点集合
     * @param adjacencyTable 路径点邻接出边表
     * @param startId        起点ID
     * @param endId          终点ID
     * @param hGetter        {@link BiFunction}泛型<当前点对象,终点对象,两点哈密顿值>
     *                       推荐: H = Math.abs(end.x - point.x) + Math.abs(end.y - point.y)
     */
    public PathFinder(Map<ID, V> vertices,
                      Map<ID, List<E>> adjacencyTable,
                      ID startId, ID endId, W zeroWeight,
                      BiFunction<V, V, W> hGetter) {
        if (startId.equals(endId)) {
            throw new RuntimeException("起点与终点不能相同");
        }
        this.vertices = vertices;
        this.adjacencyTable = adjacencyTable;
        this.endId = endId;
        this.getterOfH = hGetter;
        Wrapper wrappedStart = wrapPoint(startId, zeroWeight);
        wrappedStart.previous = prototype;
        this.openList.offer(wrappedStart);
    }

    @SuppressWarnings("unchecked")
    private Wrapper copyWrapper() {
        return (Wrapper) this.prototype.clone();
    }

    /**
     * 包装点
     *
     * @param pointId 当前点ID
     * @param g       当前点到起点的最短路径
     */
    private Wrapper wrapPoint(ID pointId, W g) {
        Map<ID, V> vertices = this.vertices;
        Wrapper wrapper = copyWrapper();
        wrapper.pointId = pointId;
        wrapper.g = g;
        wrapper.h = this.getterOfH.apply(vertices.get(pointId), vertices.get(this.endId));
        wrapper.f = g.add(wrapper.h);
        this.wrapperTable.put(pointId, wrapper);
        return wrapper;
    }

    public Map<String, Object> findPath(Function<E, ID> edgeHeadGetter,
                                        Function<E, W> edgeWeightGetter) {
        Map<ID, Wrapper> wrapperTable = this.wrapperTable;
        PriorityQueue<Wrapper> openList = this.openList;
        do {
            //取出f最小的点并出列
            Wrapper current = openList.poll();
            if (null == current)
                break;
            current.closed = Boolean.TRUE;
            if (current.pointId.equals(this.endId))
                break;
            List<E> adjacencyEdges = this.adjacencyTable.get(current.pointId);
            if (null == adjacencyEdges)
                continue;
            for (E edge : adjacencyEdges) {
                ID nextPointId = edgeHeadGetter.apply(edge);
                Wrapper next = wrapperTable.get(nextPointId);
                W nextG = current.g.add(edgeWeightGetter.apply(edge));
                if (null == next) {
                    next = wrapPoint(nextPointId, nextG);
                    next.previous = current;
                    openList.offer(next);
                } else if (nextG.compareTo(next.g) < 0) {
                    next.previous = current;
                    next.g = nextG;
                    next.f = nextG.add(next.h);
                }
            }
        } while (openList.size() > 0);
        Wrapper wrappedEnd = wrapperTable.get(this.endId);
        if (null == wrappedEnd) return null;
        Map<String, Object> ret = new HashMap<>();
        ret.put(BEELINE_WEIGHT, wrappedEnd.f);

        List<V> path = new ArrayList<>();
        //wrappedStart.previous需要设置一个占位(目前用prototype占位),
        //否则wrappedStart不会被加入path
        while (null != wrappedEnd.previous) {
            path.add(0, vertices.get(wrappedEnd.pointId));
            wrappedEnd = wrappedEnd.previous;
        }
        ret.put(BEELINE_POINT, path);
        return ret;
    }
}
