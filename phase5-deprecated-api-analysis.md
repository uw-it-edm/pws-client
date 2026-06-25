# Phase 5: Deprecated API Investigation & Analysis

## Findings

### Issue 1: javax.* imports (deprecated in Java 9+, removed in Java 17+)

**File:** `pws-client-spring-boot-starter/src/main/java/edu/uw/edm/pws/autoconfigure/PWSAutoConfiguration.java`

**Lines affected:**
```java
import javax.net.ssl.SSLContext;        // Line 9
import javax.net.ssl.TrustManager;      // Line 10
```

**Problem:**
- `javax.net.ssl` packages were moved to `java.base` module in Java 9
- In Java 17+ (our target), these are deprecated and will be removed
- Spring Framework 6+ recommends using `javax.net.ssl` is still available in Java 21 but marked for future removal

**Migration path:**
- These classes are in the `java.base` module; no import change needed (they're built-in)
- BUT: Using them directly without suppressing warnings is risky for Java 21+

**Solution:**
- Keep the imports (they're standard JDK classes in java.base)
- Add `@SuppressWarnings("deprecation")` only if compiler warnings occur
- These are NOT actually deprecated; the warning is a false positive from cross-module visibility

---

### Issue 2: HttpEntity raw type (unchecked generic warning)

**File:** `pws-client/src/main/java/edu/uw/edm/pws/impl/PersonWebServiceClientImpl.java`

**Lines affected:**
```java
HttpEntity request = new HttpEntity(headers);  // Line 57, 87
```

**Problem:**
- `HttpEntity` is a generic type: `HttpEntity<T>`
- Using it without type parameters triggers unchecked warning
- Should be: `HttpEntity<String>` or a more specific type

**Solution:**
- Replace `new HttpEntity(headers)` with `new HttpEntity<>(headers)` (diamond operator)
- Or: `new HttpEntity<String>(headers)` if type specificity helps

---

### Issue 3: Spring Framework API compatibility check

**Scope:**
- No direct Spring Framework deprecated API usage found
- RestTemplate is stable in Spring Framework 6.x (used in Boot 3.5.9)
- HttpComponentsClientHttpRequestFactory is stable in Boot 3.5.9

---

## Deprecation Action Items

| Item | Severity | Action | Risk |
|------|----------|--------|------|
| `javax.net.ssl` imports | LOW | Suppress if warning occurs; keep as-is for now | None; Java 21 still supports |
| `HttpEntity` raw type | MEDIUM | Fix with diamond operator | Low; purely compiler-level |
| Gradle deprecations | LOW | Already addressed (Spotless indentation methods) | None; fixed in commit 1e20f31 |

---

## Recommendation

1. **Fix HttpEntity raw type immediately** (low-risk, high-value code quality improvement)
2. **Monitor javax.net.ssl** in future Java versions (not urgent for Java 21)
3. **Validate runtime behavior** after fixes (tests already pass)

---

## Validation Plan

After fixes:
- `./gradlew test` (verify behavior unchanged)
- `./gradlew spotlessCheck` (verify formatting compliance)
- `./gradlew build` (full compile with warnings enabled)

