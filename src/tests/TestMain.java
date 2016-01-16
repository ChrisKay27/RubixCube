package tests;

import cube.RubixCube;

import static cube.RubixCube.*;

/**
 * Created by chris_000 on 1/12/2016.
 */
public class TestMain {
    public static void main(String[] args) {
        boolean success;
        success = RubixCubeTester.test();
        System.out.println(success);
    }
}
