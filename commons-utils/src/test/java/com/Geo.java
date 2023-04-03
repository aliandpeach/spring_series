package com;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

import java.util.Base64;

public class Geo
{
    public static void main(String[] args) throws ParseException
    {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);

        /**
         * 熟知文本WKT阅读器，可以将WKT文本转换为Geometry对象
         */
        WKTReader wktReader = new WKTReader(geometryFactory);
        Geometry geometry1 = wktReader.read("POLYGON((121.474 31.2345, 121.472 31.2333, 121.471 31.2315, 121.472 31.2302, 121.473 31.2304, 121.476 31.232, 121.474 31.2345))");
        String str = "AAAAAAEDAAAAAQAAAAUAAAAEAADAc2JeQA8AAGAMOD1A+///Tx9jXKAEAAAYZC89QP///9P1YI5A8///72DNPUAeAABmRmJeQAkAAPjBZT1ABAAAWHNiXkAPAABgDNA9QA==";
        /**
         * Geometry对象，包含Point、LineString、Polygon等子类
         */
        Geometry geometry = wktReader.read("POINT (113.53896635 22.36429837)");

        /**
         * 将二进制流的形式读取Geometry对象
         */
        WKBReader wkbReader = new WKBReader(geometryFactory);
        Geometry geometry2 = wkbReader.read(Base64.getDecoder().decode(str));

        WKBWriter wkbWriter = new WKBWriter(0);

        /**
         * 单纯的一个坐标点，单点可以创建Point，多点可以创建LineString、Polygon等
         */
        Coordinate coordinate = new Coordinate(1.00, 2.00);
        Point point = geometryFactory.createPoint(coordinate);

        Polygon polygon = geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(1, 2),
                new Coordinate(1, 2),
                new Coordinate(1, 2),
                new Coordinate(1, 2),
                new Coordinate(1, 2),
        });

        /**
         * WKT输出器，将Geometry对象写出为WKT文本
         */
        WKTWriter wktWriter = new WKTWriter();
        String write = wktWriter.write(point);

        System.out.println();
    }
}
