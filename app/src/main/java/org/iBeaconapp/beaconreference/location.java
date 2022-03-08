package org.iBeaconapp.beaconreference;

public class location {
    public static int[] xy(String distA,String distB){
        int[] location = {0,0};
        float distA_F= Float.parseFloat(distA);
        float distB_F = Float.parseFloat(distB);

        float BT = (float)(Math.round(distA_F*10.0)/10.0);
        float CT = (float)(Math.round(distB_F*10.0)/10.0);
        int size = 10; //教室
        int[][] data = new int[size+1][size+1];
        //3個beacon A,B,C
        int A = data[0][size/2] = 1;
        int B = data[size][0] = 1;
        int C = data[size][size] = 1;
        if ((BT+CT) >= size){
            int Ty = (int)Math.round((float)((BT*BT)-(CT*CT)+(size*size))/(2*size));
            int Tx = (size-(int)(Math.round(Math.pow((BT*BT)-(Ty*Ty),0.5))));
            if ((Tx<=size)&&(Tx>0)&&(Ty<=size)&&(Ty>0)){
                if(data[Tx][Ty] != 0){
                    int T = data[Tx][Ty] + 1;
                    data[Tx][Ty] = T;
                }else{
                    data[Tx][Ty] = 1;
                }
                location[0] = Tx;
                location[1] = Ty;
                //System.out.println("目標:("+Tx+","+Ty+")");
            }else{
                location[0] = -1;
                location[1] = -1;
                //System.out.println("該目標不在教室");
            }
        }else{
            location[0] = -2;
            location[1] = -2;
            //System.out.println("座標錯誤");
        }
        return location;
    }
}
