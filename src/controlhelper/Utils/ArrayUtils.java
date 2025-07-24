package controlhelper.utils;

import java.lang.reflect.Array;

import arc.struct.Seq;
import arc.func.Boolf;
import arc.struct.Queue;

public class ArrayUtils 
{
    public static <T> boolean AreSame(Queue<T> a, Queue<T>b)
    {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (a.size != b.size) return false;


        for (int i = 0; i < a.size; i++)
        {
            if (a.get(i) != b.get(i)) return false;
        }
        return true;
    }


    public static <T> Queue<T> Copy(Queue<T> a)
    {
        if (a == null) return null;
        Queue<T> out = new Queue<>();
        for (T i : a)  out.add(i);
        return out;
    }

    public static <T> Seq<T> Copy(Seq<T> a)
    {
        if (a == null) return null;
        Seq<T> out = new Seq<>();
        for (T i : a)  out.add(i);
        return out;
    }


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


    public static <T> Queue<T> RemoveAll(Queue<T> queue, Boolf<T> cond)
    {
        for (int i = 0; i < queue.size; i++)
        {
            var cur = queue.get(i);
            if (cond.get(cur))
            {
                queue.remove(cur);
                i--;
            }
        }
        return queue;
    }


    public static <T> Queue<T> AddAt(Queue<T> queue, T val, int id)
    {
        if (queue == null) return null;
        if (queue.size == 0) return queue;

        if (id > queue.size) id = queue.size;
        Queue<T> afterId = new Queue<>();
        while (queue.size > id) 
        {
            afterId.add(queue.get(id));
            queue.removeIndex(id);
        }
        queue.add(val);
        for (var i : afterId)
        {
            queue.add(i);
        }
        return queue;
    }


    public static <T> Queue<T> Replace(Queue<T> queue, T a, T b)
    {
        if (queue == null) return  null;
        if (queue.size == 0) return queue;
        int id = queue.indexOf(i -> i == a);
        if (id == -1) return queue;
        queue.removeIndex(id);
        return AddAt(queue, b, id);
    }

    public static <T> Queue<T> AddAll(Queue<T> queueA, Queue<T> queueB)
    {
        for (T i : queueB) 
        {
            queueA.add(i);
        }
        return queueA;
    }

    public static <T> Queue<T> AddAll(Queue<T> queue, Seq<T> seq)
    {
        for (T i : seq)
        {
            queue.add(i);
        }
        return queue;
    }

    public static <T> Seq<T> ToSeq(Queue<T> queue)
    {
        Seq<T> seq = new Seq<>();
        for (T i : queue) 
        {
            seq.add(i);
        }
        return seq;
    }
}
