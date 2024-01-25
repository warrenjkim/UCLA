template<typename T>
class Kontainer {
public:
  using value_type           = T;
  using reference_type       = T&;
  using pointer_type         = T*;
  using const_reference_type = const T&;
  using const_pointer_type   = const T*;
  
public:
  constexpr  Kontainer();
             ~Kontainer();

  void       add(const_reference_type element);
  value_type minimum_element() const;

private:
  value_type   m_Data[100];
  size_t       m_Size  = 0;
};


template<typename T>
constexpr Kontainer<T>::Kontainer() : m_Size(0) { }


template<typename T>
Kontainer<T>::~Kontainer() { }


template<typename T>
inline void Kontainer<T>::add(const_reference_type element)
{
  m_Data[m_Size++] = element;
}


template<typename T>
inline typename Kontainer<T>::value_type Kontainer<T>::minimum_element() const
{
  if (m_Size == 0)
      return value_type();
  
  value_type min = m_Data[0];

  for (size_t i = 0; i < m_Size; i++)
    if (m_Data[i] < min)
      min = m_Data[i];

  return min;
}
