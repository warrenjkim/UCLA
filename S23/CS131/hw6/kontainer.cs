using System;

public class Kontainer<T> where T : IComparable<T>
{
    private T[] m_Data;
    private int m_Size;

    public Kontainer()
    {
        m_Data = new T[100];
        m_Size = 0;
    }

    public void Add(T element)
    {
        if (m_Size < 100)
            m_Data[m_Size++] = element;
    }

    public T MinimumElement()
    {
        if (m_Size == 0)
            return default(T);

        T min = m_Data[0];
	
        for (int i = 0; i < m_Size; i++)
            if (m_Data[i].CompareTo(min) < 0)
                min = m_Data[i];

        return min;
    }
}