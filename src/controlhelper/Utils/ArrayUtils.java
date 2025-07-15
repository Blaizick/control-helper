package controlhelper.Utils;

import java.lang.reflect.Array;

import arc.struct.Seq;

public class ArrayUtils 
{
    public static <T> T[] Concatenate(T[] a, T[] b)
    {
        if (a == null || b == null) return null;
        
        int aLen = a.length;
        int bLen = b.length;
        
        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }    

    public static int[] Concatenate(int[] a, int[] b)
    {
        if (a == null || b == null) return null;

        int aLen = a.length;
        int bLen = b.length;

        int[] c = new int[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }   

    public static int[] ToArray(Seq<Integer> seq)
    {
        if (seq == null) return null;

        int[] arr = new int[seq.size];
        for (int i = 0; i < seq.size; i++)
        {
            arr[i] = (int)seq.get(i);
        }
        return arr;
    }
}
