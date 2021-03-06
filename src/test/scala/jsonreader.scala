import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter

import java.io._

class JsonReaderTest extends FunSuite with BeforeAndAfter
{
  val filename = "unittestconfig.json"
  try {
  /***************************************************************************
   * produce and read config file
   ***************************************************************************/
    val writer = new PrintWriter(new File(filename))
    writer.write("{\n")
    writer.write("\t\"a\": \"a\",\n")
    writer.write("\t\"b\": {\n")
    writer.write("\t\t\"c\": \"c\",\n")
    writer.write("\t\t\"d\": {\n")
    writer.write("\t\t\t\"e\": \"e\"\n")
    writer.write("\t\t}\n")
    writer.write("\t},\n")
    writer.write("\t\"a with space\": \"a with space\"\n")
    writer.write("}\n")
    writer.close
    val jsonReader = new JsonReader(filename)

  /***************************************************************************
   * unit tests
   ***************************************************************************/
    test("Parse simple value") {
      assert( jsonReader.getObj("a").value.toString === "a" )
    }

    test("Parse when key has white space") {
      assert( jsonReader.getObj("a with space").value.toString === "a with space" )
    }

    test("Parse nested value") {
      assert( jsonReader.getObj("b","c").value.toString === "c" )
    }

    test("Parse when doubly nested value") {
      assert( jsonReader.getObj("b","d","e").value.toString === "e" )
    }

    test("Stores nested Json object, then access key-value") {
      val jsonObj = jsonReader.getObj("b")
      assert( jsonObj.getObj("c").value.toString === "c" )
	}

    test("Throws exception when accessing nonexistent key") {
      val thrown = intercept[Exception] {
        jsonReader.getObj("f")
      }
      assert( thrown.getMessage === "key not found: f" )
    }

    test("Throws exception when accessing nonexistent key in nested object") {
      val thrown = intercept[Exception] {
        jsonReader.getObj("b","f")
      }
      assert( thrown.getMessage === "key not found: f" )
    }
  }
  /***************************************************************************
   * finally, delete config file
   ***************************************************************************/
  finally {
    val file = new File(filename)
    file.delete
  }
}
