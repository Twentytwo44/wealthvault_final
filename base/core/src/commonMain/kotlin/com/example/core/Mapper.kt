com.example.core
interface Mapper<IN, OUT> {
    fun map(from: IN): OUT
}