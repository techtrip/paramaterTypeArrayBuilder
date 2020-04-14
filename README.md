# ParameterTypeArrayBuilder

This simple utility classes takes a Java builder approach to to build up
the arrays in the order of input for use with JdbcOperations interface
methods that take arrays of arguments alongside parallel
`java.sql.Types` integer mappings.

Maintaining order is crucially important.

The Builder provides two basic builder methods
1. One that simply takes a combination of Object input and The Types
   mapping.
2. Another that allows the use of a Java Function for validation or
   mutating/transforming the object parameter on input.

For instance. The following code snippet will build the arrays as follows:

```
        ParameterTypeArrayBuilder pMapper = ParameterTypeArrayBuilder.newBuilder()
                .parameterWihType("SOME String", Types.VARCHAR)
                .parameterWihType(36, Types.INTEGER)
                .parameterWihType(now, Types.TIMESTAMP)
                .build();

        Object[] parameters = pMapper.getParameter();
        int[] paramaterTypes = pMapper.getParameterTypes();
```

```
 parameters -->     ["SOME String", 36, 2020-04-14]
 paramaterTypes --> [12, 4, 93]
```


The following will provide a validation check, preventing nulls from
being inserted, instead replacing them with empty strings:

```
        Function<String,String> contrivedNullToEmptyStringFunction = i -> { if (StringUtils.isEmpty(i)) return ""; return i;};

        ParameterTypeArrayBuilder pMapper = ParameterTypeArrayBuilder.newBuilder()
                .parameterWihType(null, Types.VARCHAR, contrivedNullToEmptyStringFunction) // MUTATION!! Null to ""
                .parameterWihType("Test", Types.VARCHAR, contrivedNullToEmptyStringFunction) // Do Nothing
                .parameterWihType(1000, Types.VARCHAR, Object::toString) // Int in String Out
                .parameterWihType("NormalNoMod", Types.LONGVARCHAR) // Do Nothing
                .build();

        Object[] parameters = pMapper.getParameter();
        int[] paramaterTypes = pMapper.getParameterTypes();     

```

The Parameters on input to the SQL would be transformed according to the Function Interface which is a simple Predicate. The value for `Types.STRING` is `12` and `Types.LONGVARCHAR` is `-1`.

```
 parameters -->     ["", "Test", "1000", NormalNoMod]
 paramaterTypes --> [12, 12, 12, -1]
```

Of course you don't have to provide the java convenience Function as a
parameter. You could always inline it as in the following demonstration:

Transform the Object on input or provide some validation you simply
would provide a function as demonstrated here:

For instance. Use a Function that inserts Y or N based on the input paramater starting with the Letter "S"

```
        Predicate<String> stringStartWithT = s -> s.startsWith("S");

        ParameterTypeArrayBuilder pMapper = ParameterTypeArrayBuilder.newBuilder()
                .parameterWihType(stringStartWithT.test("SOME String"), Types.BOOLEAN)
                .parameterWihType(stringStartWithT.test("Not SOME String"), Types.BOOLEAN)
                .build();

        Object[] parameters = pMapper.getParameter();      

```

The Parameters on input to the SQL would be transformed according to the Function Interface which is a simple Predicate. The value for `Types.BOOLEAN` is 16.

```
 parameters -->     [true, false]
 paramaterTypes --> [16, 16]   
```
### See the `ParameterTypeArrayBuilderTest` class for running examples

