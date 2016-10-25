package openbracket;

import java.io.*;
import java.util.*;

public class Solution2 {
    private static double getMedian(Integer[] s){
        int len = s.length;
        int mid = (len/2);
        
        if(len%2==1) return s[mid];
        return (s[mid] + s[mid+1])/2.0;
    }
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int d = in.nextInt();
        Integer s[] = new Integer[d];
        
        // Collect historical data
        for(int i=0;i<d;i++)
            s[i] = in.nextInt();
        //Arrays.sort(s);

        System.out.println(Arrays.asList(s));
        
        System.out.println(getMedian(s, 0, d-1));
        
//        int cnt = 0;
//        for(int i=d;i<n;i++){
//            double ms = getMedian(s);
//            
//            int cur = in.nextInt();
//            if(cur >= 2*ms) cnt++;
//            
//            int pos = findpos(s, 1, s.length-1, cur);
//            for(int j=0;j<pos;j++)
//                s[j] = s[j+1];
//            
//            s[pos] = cur;
//            //System.out.println(cur + "->" + Arrays.asList(s));
//        }
        in.close();
        //System.out.println(cnt);
    }
    
    private static double getMedian(Integer s[], int p, int q){
    	int len = q-p+1;
        int mid = len/2;
        
        int e1 = getElementOfRank(s, p, q, mid);
        if(len%2==1)  return e1;
        
        int e2 = getElementOfRank(s, p, q, mid+1);
        return (e1+e2)/2.0;
    }
    
    private static int getElementOfRank(Integer s[], int p, int q, int rank){
    	int r = partitionArray(s, p, q);
    	if(r==rank) return s[r];
    	else if(r<rank) return getElementOfRank(s, r, q, rank);
    	else			return getElementOfRank(s, p, r, rank);
    }
    
    
    private static int partitionArray(Integer s[], int p, int q){
        int len = q-p+1;
        int mid = len/2;
        
        while(true){
        	//System.out.println("mid=" + mid + "\tp=" + p + "\tq=" + q);
            while(s[p]<=s[mid] && p<mid){
            	p++;
            };
            while(s[q]>=s[mid] && q>mid){
            	q--;
            };
            if(p==q) break;
            
            swap(s, p, q);
            if(p==mid) mid=q;
            if(q==mid) mid=p;
        }
        return p;
    }
    
    private static void swap(Integer[]s, int a, int b){
        int tmp = s[a];
        s[a] = s[b];
        s[b] = tmp;
    }
}