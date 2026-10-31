# model-algebra

An experimental, fully model-driven framework: database tables, APIs and user interfaces are generated from
models, and models are combined with set operations, an algebra of models.

A model describes one entity (a table, a form, a type) in JSON:

```json
{
  "metadata": { "name": "User", "version": "1.0.0" },
  "fields": [
    { "name": "id", "type": "INT64", "constraints": { "primaryKey": true, "identity": "always" } },
    { "name": "email", "type": "STRING", "parameters": { "length": 100 }, "constraints": { "nullable": false, "unique": true } },
    { "name": "age", "type": "INT16", "constraints": { "min": 0 } }
  ]
}
```

The format is defined in [model-schema](https://github.com/model-algebra/model-schema).

## Projects

| Project | What it is |
|---|---|
| **model-algebra** (this repository) | Java library: model operations (union, intersection, subtraction, xor) and validation |
| [model-schema](https://github.com/model-algebra/model-schema) | JSON Schema of the model format and the catalog of canonical types |
| [model-examples](https://github.com/model-algebra/model-examples) | Example models used by the tests of all projects |
| [common-typescript](https://github.com/model-algebra/common-typescript) | TypeScript classes of models (`Model`, `ModelSet`) and the bridge interfaces |
| [model-algebra-typescript](https://github.com/model-algebra/model-algebra-typescript) | TypeScript model operations, validation, and the diff of model sets |
| [ddl-bridge-typescript](https://github.com/model-algebra/ddl-bridge-typescript) | Models ↔ PostgreSQL DDL, and migrations that bring a database to a set of models |
| [openapi-bridge-typescript](https://github.com/model-algebra/openapi-bridge-typescript) | Models ↔ OpenAPI 3.1 descriptions of CRUD APIs |
| [typescript-bridge](https://github.com/model-algebra/typescript-bridge) | Models → TypeScript interfaces, classes with validation, and factories |
| [vuetify-bridge](https://github.com/model-algebra/vuetify-bridge) | Vue 3 / Vuetify fields, forms and tables driven by models |
| [example-site](https://github.com/model-algebra/example-site) | Example site of the vuetify-bridge components |

A **bridge** converts models to a target format and, where it can, back: `toTarget(model)` and `toModel(target)`.

## Model operations

Models combine like sets of their fields:

| Operation | Java | Result |
|---|---|---|
| Union | `a.add(b)` | Fields of both models |
| Subtraction | `a.sub(b)` | Fields of `a` that `b` does not have |
| Intersection | `a.intersect(b)` | Fields both models have |
| Exclusive or | `a.xor(b)` | Fields only one of the models has |

The operations work on the whole JSON structure, level by level:

- **What gets combined:** objects by their keys, and arrays of objects with a `name` (like `fields`) by those names.
- **Nesting:** a field both sides have is combined recursively when both values are objects or named arrays.
  Otherwise the value of the first model is kept.

`processor/` in [model-examples](https://github.com/model-algebra/model-examples) has inputs and the expected result
of each operation. For example, `user_core_model.json` united with `user_extended_model.json` gives
`user_core_add_extended.json`.

## Java library

Requires Java 21. Package `org.aljabr`:

```java
IModel core = Model.from(coreJson);
IModel extended = Model.from(extendedJson);

IModel user = core.add(extended);
String json = user.toJson();

Map<String, Field> fields = user.fieldsAsMap();
```

`Validator` checks a model against the JSON Schema of the format (draft 2020-12, with
[networknt json-schema-validator](https://github.com/networknt/json-schema-validator)):

```java
List<Error> errors = new Validator().validate(json);   // empty when the model is valid
```

Bridges implement `IBridge<T>` (`toModel(T)`, `fromModel(IModel)`):

| Bridge | Status |
|---|---|
| `Json` | Implemented: model ↔ JSON text |
| `Sql`, `Jackson`, `Swagger`, `Xml` | Placeholders |

`org.aljabr.runner.App` is a small runner that connects to PostgreSQL. It reads `DATABASE_URL`, `DATABASE_USER` and
`DATABASE_PASSWORD`, and lists the tables of the public schema.

## Building

The schemas and the example models are Git submodules, and the library uses
[func-helpers](https://github.com/crimaniak/func-helpers), which is not published to a Maven repository:

```sh
git clone --recurse-submodules git@github.com:model-algebra/model-algebra.git
git clone https://github.com/crimaniak/func-helpers.git
mvn -f func-helpers/pom.xml install

cd model-algebra
mvn test
```

CI builds func-helpers at a pinned commit; see `.github/workflows/CI.yml`.

## License

MIT
