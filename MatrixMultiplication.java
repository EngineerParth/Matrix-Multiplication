import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author Parth
 */
public class MatrixMultiplication {

    /**
     * @param args the command line arguments
     */
    static int dimension=10;
    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
    	    	
    	//variable declaration
    	int[][] a;
    	int[][] b;
    	WorkerThread[] workers;
        
        //Reading actual data from file
        Scanner s=new Scanner(new File("C:/Users/Parth/workspace/MatrixMultiplication/bin/matrixdata.txt"));
        
        int dimension_local=s.nextInt();
        
        a = new int[dimension_local][dimension_local];
        b = new int[dimension_local][dimension_local];
        workers = new WorkerThread[dimension_local];
        
        //read matrix a
        for(int i=0;i<dimension_local;i++){
            for(int j=0;j<dimension_local;j++){
                a[i][j]=s.nextInt();
                //b[i][j]=s.nextInt();
            }
        }
        
        //read matrix b
        for(int i=0;i<dimension_local;i++){
            for(int j=0;j<dimension_local;j++){
                //a[i][j]=s.nextInt();
                b[i][j]=s.nextInt();
            }
        }
        
        //Computation starts with data object initialization and threads creation
        MatrixData data1=new MatrixData(a,b,dimension_local);
        for(int k=0;k<dimension_local;k++){
            workers[k] = new WorkerThread(data1,k);
        }   
        
        for(int k=0;k<dimension_local;k++){
          workers[k].t.join();  
        }
        
        for(int i=0;i<dimension_local;i++){
        	for(int j=0;j<dimension_local;j++){
        		System.out.print(data1.c[j][i]+" ");
        	}
        	System.out.println("");
        }
           
    }
}

class WorkerThread implements Runnable{
    int[] rowA,rowC;
    int d,rowIndex;
    MatrixData objData;
    Thread t;

    WorkerThread(MatrixData objData,int rowIndex){
        d=objData.a.length;
        this.objData=objData;
        this.rowIndex=rowIndex;
        t=new Thread(this);
        t.start();
        rowC=new int[d];
    }
    @Override
    public void run() {
            for(int i=0;i<d;i++){
                objData.c[rowIndex][i]=0;
                for(int j=0;j<d;j++){
                    objData.c[rowIndex][i]+=(objData.a[rowIndex][j]*objData.b[j][i]);  
                }
                rowC[i]=objData.c[rowIndex][i];
            }
            try {
				objData.addRowToC(rowC, rowIndex);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    }
}

class MatrixData{
    public int[][] a;
    public int[][] b;
    public int[][] c;
    public int d;
    
    boolean flag=false;
    
    MatrixData(int[][] a, int[][] b,int d){
        this.d=d;
        this.a = new int[d][d];
        this.a=a;
        this.b = new int[d][d];
        this.b=b;
        this.c=new int[d][d];
        
        //initializing answer matrix with all 0's
        for(int i=0;i<d;i++){
            for(int j=0;j<d;j++){
                c[i][j]=0;
            }
        }
       
    }
    
    //This method will be used by the worker threads to add their computed results to the appropriate row
    //index in the result matrix C
    synchronized void addRowToC(int[] rowC,int rowIndex) throws InterruptedException{
    	while(flag){
    		wait();
    	}
    	flag=true;
    	for(int i=0;i<d;i++){
    		c[rowIndex][i]=rowC[i];
    	}
    	flag=false;
    	notify();
    }
}

