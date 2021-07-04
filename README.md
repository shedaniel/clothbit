# Clothbit Config API
A feature-complete, useful configuration library, designed with customization and scalability in mind.
___
### Options & Serializer
A config option may be a field, or a composite of different options, an option often consists of different properties.

A config serializer will handle converting to/from different formats.

A custom JSON5 serializer will be written for this.

**On fabric:** the different serializers will be provided as separate binaries.
- GSON
- Json5

# Simple Types
- Byte
- Short
- Double
- Float
- Int
- Long
- BigInteger
- BigDecimal
- Boolean
- Character
- String

# Extended Types
- Identifier
- Instant (Time Selector)
- Tag
- Text (Rich Text Editor)
- Enum<?>

# Complex Types
Compound (Object/Map), Array (List) may be supported with nested option types.

# Java POJO
Clothbit will offer an annotation based, java JOJO-like way to construct configs, similar to Auto Config.
```java
// Drafted annotations
@Config
@Comment
@Sync // applied to the whole class or fields
```

# Gui
Config Screens have always been a big part of Cloth Config, Clothbit is no different. Clothbit this time is offering some essential changes:
1. Option type based GUI entry constructor
2. Easy registration for option types
3. Tooltips for simple stuff, collapsible descriptions for the actual detailed explanation with possible for links and images
4. Rich Text WYSIWYG editors
   Clothbit.md
   3 KB