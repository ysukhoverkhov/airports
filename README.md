### Environment

The following tools are required:

- `sbt`

### Run

In the project's root folder, execute:

- `sbt web/run`

Open the browser at `localhost:8080`

### Design considerations

- Pure FP approach (to encode as much proves in code as possible and to couple components as less as possible)
- Algebras implemented as tagless-final (because less boilerplate comparing to Free)
- DI as nested algebras (because it's obvious)
- Common algorithms extracted in a shared project (because it's common sense)
- Elm for UI (without any particular reasons)
- Property based tests where appropriate (to make sure particular algebras are defined on the whole domain)
- No mocking library in tests (because they suck), dedicated implementations of various low level algebras instead

### Limitations

- Error handling is not verbose
- CSV parser is super slow
- Reports/Queries are performed on data in memory, so no handling for bigger data. But could be changed to any SQL/NoSQL easely
- Property based tests mixed with unit tests, could not be suitable for large scale project
- Elm project is injected in a hacky way (without proper sbt-web project)
- Some version incompatibility in libs
- Web Service lacks contract testing