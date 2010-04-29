/*Copyright (C) 2009,2010 Brian Satzinger

This file is part of Scopeduino.

    Scopeduino is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Scopeduino is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Scopeduino.  If not, see <http://www.gnu.org/licenses/>.
*/

package scopeduino.display;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import serial.LogicDAQ;
/**
 *
 * @author brian
 */
public class TraceReader extends Thread {

    Vector<Trace> traces;
    LogicDAQ arduino;

    ConcurrentLinkedQueue<byte[]> commandQueue;

    public TraceReader(Vector<Trace> t, LogicDAQ a)
    {
        traces = t;
        arduino = a;

        commandQueue = new ConcurrentLinkedQueue<byte[]>();
    }

    public void run()
    {
     

        //Used to calculate the trace rate
        int ntraces = 0;
        long time = System.currentTimeMillis();

        while (arduino.connected)
        {
            Vector<int[]> data = arduino.readTrace();

            int[] dtrace = data.elementAt(0);
            int[] btrace = data.elementAt(1);
            int[] ctrace = data.elementAt(2);

            //Create 8 traces, one for each bit
            Vector<Trace> tBuffer = new Vector<Trace>(18);


            //handle traces in d2...d7
            int mask = 1;

            for (int b = 2; b <= 7; b++)
            {
                //calculate the bit mask
                mask = 1 << b;

                int[] binaryString = new int[dtrace.length];

                for (int i = 0; i < dtrace.length; i++)
                {
                    if((mask & dtrace[i]) == 0)
                    {
                        binaryString[i] = 0;
                    }
                    else
                    {
                        binaryString[i] = 1;
                    }
                }

                

                double[] dispData = new double[dtrace.length];

                //Calculate the display coordinates for this trace
                for (int i = 0; i < dtrace.length; i++)
                {
                    dispData[i] = b / 12.0 + binaryString[i] / 24.0 - 0.95;
                }

                //Create a trace with this data
                float tr, tg, tb;

                //alternate trace colors to make it easier to see
                if (tBuffer.size() % 2 == 0)
                {
                    tr = 0.2f;
                    tg = 0.2f;
                    tb = 1.0f;
                }
                else
                {
                    tr = 0.2f;
                    tg = 1.0f;
                    tb = 0.2f;
                }

                //Create a trace with this data
                Trace t = new Trace(dispData,tr, tg, tb, 0.25f,1);

                t.label = b + " in";
                t.labely = (float)b / 12.0f - 0.95f;

                //Add the trace to the buffer
                tBuffer.add(t);
            }


            //handle traces in b0 to b3 (inputs) and b4 to b5 (outputs)
            for (int b = 0; b <= 5; b++)
            {
                //calculate the bit mask
                mask = 1 << b;

                int[] binaryString = new int[btrace.length];

                for (int i = 0; i < btrace.length; i++)
                {
                    if((mask & btrace[i]) == 0)
                    {
                        binaryString[i] = 0;
                    }
                    else
                    {
                        binaryString[i] = 1;
                    }
                }



                double[] dispData = new double[btrace.length];

                //Calculate the display coordinates for this trace
                for (int i = 0; i < btrace.length; i++)
                {
                    if (b < 4)  //inputs
                    {
                        dispData[i] = (b + 8) / 12.0 + binaryString[i] / 24.0 - 0.95;
                    }
                    else
                    {
                        dispData[i] = (b + 10) / 12.0 + binaryString[i] / 24.0 - 0.95;
                    }
                }

                //Create a trace with this data
                float tr, tg, tb;

                //alternate trace colors to make it easier to see
                if (tBuffer.size() % 2 == 0)
                {
                    tr = 0.2f;
                    tg = 0.2f;
                    tb = 1.0f;
                }
                else
                {
                    tr = 0.2f;
                    tg = 1.0f;
                    tb = 0.2f;
                }

                //Create a trace with this data
                Trace t = new Trace(dispData,tr, tg, tb, 0.25f,1);

                if (b < 4)
                {
                    t.label = (b + 8) + " in";
                    t.labely = (float)(b + 8) / 12.0f - 0.95f;
                }
                else
                {
                    t.label = (b + 8) + " out";
                    t.labely = (float)(b + 10) / 12.0f - 0.95f;
                }



                //Add the trace to the buffer
                tBuffer.add(t);
            }


            //handle traces in c0...c6
            for (int b = 0; b <= 5; b++)
            {
                //calculate the bit mask
                mask = 1 << b;

                int[] binaryString = new int[ctrace.length];

                for (int i = 0; i < ctrace.length; i++)
                {
                    if((mask & ctrace[i]) == 0)
                    {
                        binaryString[i] = 0;
                    }
                    else
                    {
                        binaryString[i] = 1;
                    }
                }



                double[] dispData = new double[ctrace.length];

                //Calculate the display coordinates for this trace
                for (int i = 0; i < ctrace.length; i++)
                {
                    dispData[i] = (b + 17) / 12.0 + binaryString[i] / 24.0 - 0.95;
                }

                float tr, tg, tb;

                //alternate trace colors to make it easier to see
                if (tBuffer.size() % 2 == 0)
                {
                    tr = 0.2f;
                    tg = 0.2f;
                    tb = 1.0f;
                }
                else
                {
                    tr = 0.2f;
                    tg = 1.0f;
                    tb = 0.2f;
                }

                //Create a trace with this data
                Trace t = new Trace(dispData,tr, tg, tb, 0.25f,1);

                t.label = "a" + b + " out";
                t.labely = (float)(b + 17) / 12.0f - 0.95f;


                //Add the trace to the buffer
                tBuffer.add(t);
            }


            //Add them to traces
            synchronized(traces)
            {
                //Empty the buffer
                traces.clear();

                for (Trace tr : tBuffer)
                {
                    traces.add(tr);
                }
            }

            try
            {
                this.sleep(1);
            }
            catch (Exception e)
            {
                System.err.println(e);
            }

        }
        System.err.println("TraceReader terminated because arduino disconnected");
    }

}
