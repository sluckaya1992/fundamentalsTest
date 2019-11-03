import com.beust.klaxon.*
import java.io.StringReader
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OrdersAnalyzer {

    data class Order(val orderId: Int,val creationDate: LocalDateTime, val orderLines: List<OrderLine>)

    data class OrderLine(val productId: Int, val name: String, val quantity: Int, val unitPrice: BigDecimal)

    fun totalDailySales(orders: List<Order>): Map<DayOfWeek, Int> {
        val map = mutableMapOf<DayOfWeek, Int>()
        for (order in orders) {
            val dayOfWeek = order.creationDate.dayOfWeek
            if (dayOfWeek != null && map[dayOfWeek] == null){
                map[dayOfWeek] = 0
            }
            for (orderLine in order.orderLines) {
                map[dayOfWeek] = map[dayOfWeek]!! + orderLine.quantity
            }
        }
        return map
    }
}

fun main() {
    val klaxon = Klaxon()
    val result = mutableListOf<OrdersAnalyzer.Order>()
    JsonReader(StringReader(orders)).use { reader ->
        reader.beginArray {
            while (reader.hasNext()) {
                val order = klaxon.converter(dateConverter).parse<OrdersAnalyzer.Order>(reader)
                if (order != null) {
                    result.add(order)
                }
            }
        }
    }
    val orders: List<OrdersAnalyzer.Order> = result
    val orderAnalyser = OrdersAnalyzer()
    println(orderAnalyser.totalDailySales(orders))
}

const val orders = """[
    {
        "orderId": 554,
        "creationDate": "2017-03-25T10:35:20", 
        "orderLines": [
            {"productId": 9872, "name": "Pencil", "quantity": 3, "unitPrice": 3.00}
        ]
    },
    {
        "orderId": 555,
        "creationDate": "2017-03-25T11:24:20", 
        "orderLines": [
            {"productId": 9872, "name": "Pencil", "quantity": 2, "unitPrice": 3.00},
            {"productId": 1746, "name": "Eraser", "quantity": 1, "unitPrice": 1.00}
        ]
    },
    {
        "orderId": 453,
        "creationDate": "2017-03-27T14:53:12", 
        "orderLines": [
            {"productId": 5723, "name": "Pen", "quantity": 4, "unitPrice": 4.22},
            {"productId": 9872, "name": "Pencil", "quantity": 3, "unitPrice": 3.12},
            {"productId": 3433, "name": "Erasers Set", "quantity": 1, "unitPrice": 6.15}
        ]
    },
    {
        "orderId": 431,
        "creationDate": "2017-03-20T12:15:02", 
        "orderLines": [
            {"productId": 5723, "name": "Pen", "quantity": 7, "unitPrice": 4.22},
            {"productId": 3433, "name": "Erasers Set", "quantity": 2, "unitPrice": 6.15}
        ]
    },
    {
        "orderId": 690,
        "creationDate": "2017-03-26T11:14:00", 
        "orderLines": [
            {"productId": 9872, "name": "Pencil", "quantity": 4, "unitPrice": 3.12},
            {"productId": 4098, "name": "Marker", "quantity": 5, "unitPrice": 4.50}

        ]
    }
]
"""

val dateConverter = object: Converter {
    override fun canConvert(cls: Class<*>)
            = cls == LocalDateTime::class.java

    override fun fromJson(jv: JsonValue) =
        if (jv.string != null) {
            LocalDateTime.parse(jv.string, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
        } else {
            throw KlaxonException("Couldn't parse date: ${jv.string}")
        }

    override fun toJson(value: Any)
            = """ { "date" : $value } """
}
