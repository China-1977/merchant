import com.intellij.database.model.DasTable
import com.intellij.database.util.Case
import com.intellij.database.util.DasUtil

/*
 * Available context bindings:
 *   SELECTION   Iterable<DasObject>
 *   PROJECT     project
 *   FILES       files helper
 */

packageName = "com.onss.domain;"
typeMapping = [
        (~/(?i)int/)                      : "Long",
        (~/(?i)float|double|decimal|real/): "Double",
        (~/(?i)datetime|timestamp/)       : "Timestamp",
        (~/(?i)date/)                     : "LocalDate",
        (~/(?i)time/)                     : "Time",
        (~/(?i)/)                         : "String"
]

FILES.chooseDirectoryAndSave("Choose directory", "Choose where to organization generated files") { dir ->
    SELECTION.filter { it instanceof DasTable }.each { generate(it, dir) }
}

def generate(table, dir) {
    def className = javaName(table.getName(), true)
    def fields = calcFields(table)
    new File(dir, className + ".java").withPrintWriter { out -> Entity(out, className, fields) }
    new File(dir, className + "Repository.java").withPrintWriter { out -> JpaRepository(out, className) }
}

def Entity(out, className, fields) {
    out.println "package $packageName"
    out.println ""
    out.println "import lombok.Getter;"
    out.println "import lombok.Setter;"
    out.println "import lombok.extern.log4j.Log4j2;"
    out.println "import org.springframework.data.annotation.CreatedDate;"
    out.println "import org.springframework.data.annotation.LastModifiedDate;"
    out.println "import org.springframework.data.jpa.domain.support.AuditingEntityListener;"
    out.println ""
    out.println "import javax.persistence.*;"
    out.println "import java.io.Serializable;"
    out.println "import java.sql.Timestamp;"
    out.println "import java.time.LocalDate;"
    out.println ""
    out.println "@Log4j2"
    out.println "@Getter"
    out.println "@Setter"
    out.println "@Entity"
    out.println "@EntityListeners(AuditingEntityListener.class)"
    out.println "public class $className implements Serializable {"
    out.println ""
    fields.each() {
        it.annos.each() {
            out.println "    ${it}"
        }
        out.println "    private ${it.type} ${it.name};"
    }
    out.println "}"
}

def JpaRepository(out, className) {
    out.println "package $packageName"
    out.println ""
    out.println "import org.springframework.data.jpa.repository.JpaRepository;"
    out.println "import org.springframework.data.jpa.repository.JpaSpecificationExecutor;"
    out.println "import org.springframework.data.querydsl.QuerydslPredicateExecutor;"
    out.println "import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;"
    out.println "import org.springframework.data.querydsl.binding.QuerydslBindings;"
    out.println ""
    out.println "import java.sql.Timestamp;"
    out.println "import java.util.Iterator;"
    out.println "import java.util.Optional;"
    out.println ""
    out.println "public interface ${className}Repository extends JpaRepository<${className}, Long>, JpaSpecificationExecutor<${className}>, QuerydslPredicateExecutor<${className}>, QuerydslBinderCustomizer<Q${className}> {"
    out.println ""
    out.println "    default void customize(QuerydslBindings bindings, Q${className} q${className}) {"
    out.println "        bindings.bind(q${className}.id).withDefaultBinding();"
    out.println "        bindings.bind(q${className}.insertTime).all(((path, value) -> {"
    out.println "            Iterator<? extends Timestamp> iterator = value.iterator();"
    out.println "            if (value.size() == 1) {"
    out.println "                return Optional.ofNullable(path.eq(iterator.next()));"
    out.println "            }"
    out.println "            return Optional.ofNullable(path.between(iterator.next(), iterator.next()));"
    out.println "        }));"
    out.println "        bindings.bind(q${className}.updateTime).all(((path, value) -> {"
    out.println "            Iterator<? extends Timestamp> iterator = value.iterator();"
    out.println "            if (value.size() == 1) {"
    out.println "                return Optional.ofNullable(path.eq(iterator.next()));"
    out.println "            }"
    out.println "            return Optional.ofNullable(path.between(iterator.next(), iterator.next()));"
    out.println "        }));"
    out.println "    }"
    out.println ""
    out.println "}"
}

def calcFields(table) {
    DasUtil.getColumns(table).reduce([]) { fields, col ->
        def spec = Case.LOWER.apply(col.getDataType().getSpecification())
        def typeStr = typeMapping.find { p, t -> p.matcher(spec).find() }.value
        def name = javaName(col.getName(), false);
        def annos = [];
        switch (name) {
            case "id":
                annos.add("@Id")
                annos.add("@GeneratedValue(strategy = GenerationType.IDENTITY)"); break
            case "insertTime": annos.add("@CreatedDate"); break
            case "updateTime": annos.add("@LastModifiedDate"); break
        }
        fields += [[name: name, type: typeStr, annos: annos]]
    }
}

def javaName(str, capitalize) {
    def s = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)
            .collect { Case.LOWER.apply(it).capitalize() }
            .join("")
            .replaceAll(/[^\p{javaJavaIdentifierPart}[_]]/, "_")
    capitalize || s.length() == 1 ? s : Case.LOWER.apply(s[0]) + s[1..-1]
}
