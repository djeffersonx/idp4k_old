# id4k-aop

This project has as key objective transform the id4k-core in aspect oriented programming. Encapsulating the `IdempotenceManager` inside an Advice that has other responsibilities like extract the key and the group from the annotation and the method parameters.   

### Example

In this example below we are defining an Idempotent process, using the `@IdempotenceConfig` annotation to guarantee the idempotence of this process.

```kotlin
@Component
class TransferService {

    @IdempotentResource(key = "#{key}", make = "onAlreadyExecutedFunction")
    fun transfer(key: String, from: String, to: String, amount: BigDecimal): String {
        return "Transferred $amount from: $from to: $to with success"
    }

    fun onAlreadyExecutedFunction(key: String, from: String, to: String, amount: BigDecimal): String {
        return "Transfer of $amount from: $from to: $to already executed"
    }

}
```

### About the annotation:

```kotlin
    @IdempotentResource(
        key = "#{key}", 
        group= "transfer",
        make = "onAlreadyExecutedFunction"
    )
```
#### Annotation properties:
- key
  - Defines the key used to process the idempotence
  - Can be a fixed string (not recommended)
  - Can be an expression to get the value from some method parameter
  - If not defined will automatically generated based on the hashCodes of all the method parameters
    - [See how the key is generated on this class: GetIdempotenceKey.kt](src%2Fmain%2Fkotlin%2Fbr%2Fcom%2Fidws%2Fidp4k%2Fspring%2Faop%2FGetIdempotenceKey.kt)
    
- group
  - Defines the group used to process the idempotence
  - Can be a fixed string
  - Can be an expression to get the value from some method parameter
  - If not defined will automatically generated usin the class name
    - [See how the key is generated on this class: GetIdempotenceGroup.kt](src%2Fmain%2Fkotlin%2Fbr%2Fcom%2Fidws%2Fidp4k%2Fspring%2Faop%2FGetIdempotenceGroup.kt)
- onAlreadyExecutedFunction
  - The method to be executed when the main method was already executed
  - Attention: This method should have the same signature of the main method (the method thas has the `@IdempotenceConfig` annotation)
