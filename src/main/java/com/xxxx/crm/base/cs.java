package com.xxxx.crm.base;

public class cs {
    public static void main(String[] args) {
        A a = new A();

        if(null!=a){
            System.out.println("1");
        }else {
            System.out.println("2");
        }

    }

    static class A{
        int a =1;

        public A() {

        }
    }
}
