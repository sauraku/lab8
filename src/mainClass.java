/**
 * Created by skwow on 11/6/2016.
 * Saurabh Kumar 2015088
 * Vanshit Gupta 2015186
 */
public class mainClass
{
    public static void main(String[] args)
    {
        Producer3 p3=new Producer3();
        Producer2 p2=new Producer2(p3);
        Producer1 p1=new Producer1(p2);
        Consumer1 c1= new Consumer1();
        Consumer2 c2= new Consumer2();
        Consumer3 c3= new Consumer3();
        Consumer4 c4= new Consumer4();
        p1.addConsumer(c1);p1.addConsumer(c2);p1.addConsumer(c3);p1.addConsumer(c4);
        p2.addConsumer(c1);p2.addConsumer(c2);p2.addConsumer(c3);p2.addConsumer(c4);
        p3.addConsumer(c1);p3.addConsumer(c2);p3.addConsumer(c3);p3.addConsumer(c4);

               /* c1.addProducer(p1);c1.addProducer(p2);c1.addProducer(p3);
                c2.addProducer(p1);c2.addProducer(p2);c2.addProducer(p3);
                c3.addProducer(p1);c3.addProducer(p2);c3.addProducer(p3);*/
        Thread tp1= new Thread(p1);
        Thread tp2= new Thread(p2);
        Thread tp3= new Thread(p3);
        Thread tc1= new Thread(c1);
        Thread tc2= new Thread(c2);
        Thread tc3= new Thread(c3);
        Thread tc4= new Thread(c4);
        tp1.start();
        tp2.start();
        tp3.start();
        tc1.start();
        tc2.start();
        tc3.start();
        tc4.start();
        //System.out.println(tp1.getState());
        try {
            tp1.join();
            tp2.join();
            tp3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
