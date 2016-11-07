import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by skwow on 11/6/2016.
 * Saurabh Kumar 2015088
 * Vanshit Gupta 2015186
 */
public class Producer2 implements Producer,Runnable {
    private ArrayList<Consumer> consumers= new ArrayList<>();
    private float[][] humidity = new float[5][100];
    private float minHumidity=-1000;
    private float maxHumidity=-1000;
    private float avHumidity=-1000;
    private float nextHumidity=-1000;

    private ReentrantLock myLock = new ReentrantLock();
    private Condition myCondition = myLock.newCondition();
    private ReentrantLock myLockNotify = new ReentrantLock();
    private Condition myConditionNotify = myLockNotify.newCondition();
    private Producer3 notifyto;

    public Producer2(Producer3 p)
    {
        readData();
        notifyto=p;
    }

    @Override
    public void readData() {
        try{
            FileInputStream file=new FileInputStream("humidity");
            BufferedReader fileReader =new BufferedReader(new InputStreamReader(file));
            String t;
            int i=0,j=0;
            while ((t = fileReader.readLine())!=null){
                if(t.equals(""))
                {
                    i++;
                    j=0;
                    continue;
                }
                humidity[i][j++]=Float.parseFloat(t);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addConsumer(Consumer p) {
        consumers.add(p);
        p.addProducer(this);
    }

    @Override
    public void printResult() {
        System.out.println("Humidity: max="+maxHumidity+" min="+minHumidity+" average="+avHumidity+" next="+nextHumidity);
        minHumidity=-1000;
        maxHumidity=-1000;
        avHumidity=-1000;
        nextHumidity=-1000;
    }

    @Override
    public boolean signal() {
        if( myLock.tryLock()) {
            try {
                myCondition.signalAll();
            } finally {
                myLock.unlock();
                return true;
            }
        }else
        {
            return false;
        }
    }

    @Override
    public void receiveMinData(float _minTemp, float _minHumidity, float _minRainfall) {
        minHumidity=_minHumidity;
    }

    @Override
    public void receiveMaxData(float _maxTemp, float _maxHumidity, float _maxRainfall) {
        maxHumidity=_maxHumidity;
    }

    @Override
    public void receiveAvData(float _avTemp, float _avHumidity, float _avRainfall) {
        avHumidity=_avHumidity;
    }

    @Override
    public void receiveNextData(float _nextTemp, float _nextHumidity, float _nextRainfall) {
        nextHumidity=_nextHumidity;
    }

    @Override
    public void run() {
        myLock.lock();
        myLockNotify.lock();
        for(int t=0;t<5;t++)
        {
            for(int i=0;i<consumers.size();i++ )
            {
                consumers.get(i).receiveHumidityData(humidity[t]);
            }
            for(int i=0;i<consumers.size();i++ )
            {
                if(!consumers.get(i).signal()){
                    /* try {
                        myCondition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                }
            }
            try
            {
                Thread.sleep((long) (Math.random()*5000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(int i=0;i<consumers.size();i++ )
            {
                if(!consumers.get(i).signal()){}
            }
            try
            {
                while(minHumidity==-1000 || maxHumidity==-1000 || avHumidity==-1000 ||nextHumidity==-1000) {
                    myCondition.await();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!isTempPrinted) {

                try {
                    myConditionNotify.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                isTempPrinted=false;
            }
            printResult();
            notifyto.signalNotify();
        }
    }
    private boolean isTempPrinted=false;

    public void signalNotify()
    {
        if( myLockNotify.tryLock()) {
            try {
                myConditionNotify.signalAll();
            } finally {
                myLockNotify.unlock();
            }
        }
        else
        {
            isTempPrinted=true;
        }
    }




}
