import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by skwow on 11/6/2016.
 * Saurabh Kumar 2015088
 * Vanshit Gupta 2015186
 */
public class Consumer1 implements Consumer,Runnable {

    private ArrayList<Producer> producers= new ArrayList<>();
    private float minTemp;
    private float minHumidity;
    private float minRainfall;

    private float[] temp=null;
    private float[] humidity=null;
    private float[] rainfall=null;

    private ReentrantLock myLock = new ReentrantLock();
    private Condition myCondition = myLock.newCondition();



    @Override
    public void run() {
        myLock.lock();
        //System.out.println("lock taken by c1run");
        while(true)
        {
            try
            {
                Thread.sleep((long) (Math.random()*2000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(int i=0;i<producers.size();i++) {
                if (!producers.get(i).signal()) {
                }
            }
            try
            {
                while(temp==null || humidity==null || rainfall==null) {
                  //  System.out.println("lock in await by c1run");
                    myCondition.await();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dostuff();
            for(int i=0;i<producers.size();i++)
            {
                if(!producers.get(i).signal()){
                    //System.out.println("signalling producer "+(i+1)+" from c1run failed");
                   /* try {
                        myCondition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                }
            }
          //  System.out.println("repeating c1run loop");
        }
    }


    @Override
    public void receiveTempData(float[] temp) {
        this.temp=temp;
      //  System.out.println("recievedTempdatainc1");

    }

    @Override
    public void receiveHumidityData(float[] humidity) {
        this.humidity=humidity;
       // System.out.println("recievedHumiditydatainc1");
    }

    @Override
    public void receiveRainfallData(float[] rainfall) {
        this.rainfall=rainfall;
       // System.out.println("recievedRainfalldatainc1");
    }

    @Override
    public boolean signal() {
        if( myLock.tryLock()) {
           // System.out.println("a");
            try {
                myCondition.signalAll();
            } finally {
                myLock.unlock();
                return true;
            }
        }else
        {
            //System.out.println("-a");
            return false;
        }
    }


    @Override
    public void dostuff() {
        minTemp =100000;
        minHumidity =100000;
        minRainfall =100000;
        for(int i=0;i<100;i++) {if(temp[i]<minTemp){minTemp=temp[i];}}
        for(int i=0;i<100;i++) {if(humidity[i]<minHumidity){minHumidity=humidity[i];}}
        for(int i=0;i<100;i++) {if(rainfall[i]<minRainfall){minRainfall=rainfall[i];}}
        for(int i=0;i<producers.size();i++)
        {
            producers.get(i).receiveMinData(minTemp,minHumidity,minRainfall);
        }
        temp=null;
        humidity=null;
        rainfall=null;
    }

    @Override
    public void addProducer(Producer p) {
        producers.add(p);
    }


}
