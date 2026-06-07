> **Note**
>
> This version of the template contains some help and explanations. It
> is used for familiarization with arc42 and the understanding of the
> concepts. For documentation of your own system you use better the
> *plain* version.

Introduction and Goals
======================

The tool is intended to run a quizz with multiple participants (quizzers).

The moderator can guide and synchronize multiple quizzers through a quizz by enabling questions step by step.
This way all quizzlers are working on the same question simultaniously and if and only if the moderator enables the next question for all of them, they can proceed together with the quizz. 

Requirements Overview
---------------------

-   The quizzers have to join a quizz
-   The moderator navigates through the questions
-   Whenever a participant submits an answer the evaluation is displayed as response to the participant
-   The moderator gets a statistic that shows the distribution of selected answers

Quality Goals 
-------------

**Contents.**

The top three (max five) quality goals for the architecture whose
fulfillment is of highest importance to the major stakeholders. We
really mean quality goals for the architecture. Don’t confuse them with
project goals. They are not necessarily identical.

**Motivation.**

You should know the quality goals of your most important stakeholders,
since they will influence fundamental architectural decisions. Make sure
to be very concrete about these qualities, avoid buzzwords. If you as an
architect do not know how the quality of your work will be judged …

**Form.**

A table with quality goals and concrete scenarios, ordered by priorities

Stakeholders
------------

**Contents.**

Explicit overview of stakeholders of the system, i.e. all person, roles
or organizations that

-   should know the architecture

-   have to be convinced of the architecture

-   have to work with the architecture or with code

-   need the documentation of the architecture for their work

-   have to come up with decisions about the system or its development

**Motivation.**

You should know all parties involved in development of the system or
affected by the system. Otherwise, you may get nasty surprises later in
the development process. These stakeholders determine the extent and the
level of detail of your work and its results.

**Form.**

Table with role names, person names, and their expectations with respect
to the architecture and its documentation.

| Role/Name   | Contact                   | Expectations              |
| ----------- | ------------------------- | ------------------------- |
| Trainer / Andreas Kleinbichler    | AndiKleini                  | *&lt;Wants to check whether all participants share a common understanding of the topics.&gt;   |
| Role-2      | Contact-2                 | *&lt;Expectation-2*&gt;   |

Architecture Constraints
========================

**Contents.**

Any requirement that constrains software architects in their freedom of
design and implementation decisions or decision about the development
process. These constraints sometimes go beyond individual systems and
are valid for whole organizations and companies.

**Motivation.**

Architects should know exactly where they are free in their design
decisions and where they must adhere to constraints. Constraints must
always be dealt with; they may be negotiable, though.

**Form.**

Simple tables of constraints with explanations. If needed you can
subdivide them into technical constraints, organizational and political
constraints and conventions (e.g. programming or versioning guidelines,
documentation or naming conventions)

System Scope and Context
========================

**Contents.**

System scope and context - as the name suggests - delimits your system
(i.e. your scope) from all its communication partners (neighboring
systems and users, i.e. the context of your system). It thereby
specifies the external interfaces.

If necessary, differentiate the business context (domain specific inputs
and outputs) from the technical context (channels, protocols, hardware).

**Motivation.**

The domain interfaces and technical interfaces to communication partners
are among your system’s most critical aspects. Make sure that you
completely understand them.

**Form.**

Various options:

-   Context diagrams

-   Lists of communication partners and their interfaces.

Business Context
----------------

**Contents.**

Specification of **all** communication partners (users, IT-systems, …)
with explanations of domain specific inputs and outputs or interfaces.
Optionally you can add domain specific formats or communication
protocols.

**Motivation.**

All stakeholders should understand which data are exchanged with the
environment of the system.

**Form.**

All kinds of diagrams that show the system as a black box and specify
the domain interfaces to communication partners.

Alternatively (or additionally) you can use a table. The title of the
table is the name of your system, the three columns contain the name of
the communication partner, the inputs, and the outputs.

**&lt;Diagram or Table&gt;**

**&lt;optionally: Explanation of external domain interfaces&gt;**

Technical Context
-----------------

**Business Context**

```mermaid
C4Context
  title Context diagram – Quizzler

  UpdateLayoutConfig($c4ShapeInRow="2", $c4BoundaryInRow="1")

  Person(quizzer, "Quizzer", "Answers quiz questions")
  Person(moderator, "Moderator", "Navigates through a quiz")
  Person(author, "Author", "Creates questions")

  System(quizzler, "Quiz App", "Provides an online quiz for participants")

  Rel(quizzer, quizzler, "Answers questions, receives feedback")
  Rel(author, quizzler, "Creates and maintains quiz")
  Rel(moderator, quizzler, "Navigates through a quiz")
```

**Contents.**

Technical interfaces (channels and transmission media) linking your
system to its environment. In addition a mapping of domain specific
input/output to the channels, i.e. an explanation with I/O uses which
channel.

**Motivation.**

Many stakeholders make architectural decision based on the technical
interfaces between the system and its context. Especially infrastructure
or hardware designers decide these technical interfaces.

**Form.**

E.g. UML deployment diagram describing channels to neighboring systems,
together with a mapping table showing the relationships between channels
and input/output.

**&lt;Diagram or Table&gt;**

**&lt;optionally: Explanation of technical interfaces&gt;**

**&lt;Mapping Input/Output to Channels&gt;**

Solution Strategy
=================

**Product iterations**

It is planned to deliver the product in iterations:

- ***Version 1:*** 
Users can enter a single quizzround and go through the questions on their own. After each submission the result is diplayed.

**Technology stack**
- Java Springboot for API
- Angular for FE (Web)
- PostgreSQL

Building Block View
===================

**quizzlerapi**

Provides the server side backend to the frontend.

**Content.**

The building block view shows the static decomposition of the system
into building blocks (modules, components, subsystems, classes,
interfaces, packages, libraries, frameworks, layers, partitions, tiers,
functions, macros, operations, datas structures, …) as well as their
dependencies (relationships, associations, …)

This view is mandatory for every architecture documentation. In analogy
to a house this is the *floor plan*.

**Motivation.**

Maintain an overview of your source code by making its structure
understandable through abstraction.

This allows you to communicate with your stakeholder on an abstract
level without disclosing implementation details.

**Form.**

The building block view is a hierarchical collection of black boxes and
white boxes (see figure below) and their descriptions.

![Hierarchy of building blocks](images/05_building_blocks-EN.png)

**Level 1** is the white box description of the overall system together
with black box descriptions of all contained building blocks.

**Level 2** zooms into some building blocks of level 1. Thus it contains
the white box description of selected building blocks of level 1,
together with black box descriptions of their internal building blocks.

**Level 3** zooms into selected building blocks of level 2, and so on.

Whitebox Overall System
-----------------------

quizzlerui

quizzlerapi

quizzlerdb

Here you describe the decomposition of the overall system using the
following white box template. It contains

-   an overview diagram

-   a motivation for the decomposition

-   black box descriptions of the contained building blocks. For these
    we offer you alternatives:

    -   use *one* table for a short and pragmatic overview of all
        contained building blocks and their interfaces

    -   use a list of black box descriptions of the building blocks
        according to the black box template (see below). Depending on
        your choice of tool this list could be sub-chapters (in text
        files), sub-pages (in a Wiki) or nested elements (in a modeling
        tool).

-   (optional:) important interfaces, that are not explained in the
    black box templates of a building block, but are very important for
    understanding the white box. Since there are so many ways to specify
    interfaces why do not provide a specific template for them. In the
    worst case you have to specify and describe syntax, semantics,
    protocols, error handling, restrictions, versions, qualities,
    necessary compatibilities and many things more. In the best case you
    will get away with examples or simple signatures.

***&lt;Overview Diagram&gt;***

Motivation

:   *&lt;text explanation&gt;*

Contained Building Blocks

:   *&lt;Description of contained building block (black boxes)&gt;*

Important Interfaces

:   *&lt;Description of important interfaces&gt;*

Insert your explanations of black boxes from level 1:

If you use tabular form you will only describe your black boxes with
name and responsibility according to the following schema:

| **Name**             | **Responsibility**                           |
| -------------------- | -------------------------------------------- |
| Black Box 1          |  *&lt;Text&gt;*                              |
| Black Box 2          |  *&lt;Text&gt;*                              |

If you use a list of black box descriptions then you fill in a separate
black box template for every important building block . Its headline is
the name of the black box.

### &lt;Name black box 1&gt; 

Here you describe &lt;black box 1&gt; according the the following black
box template:

-   Purpose/Responsibility

-   Interface(s), when they are not extracted as separate paragraphs.
    This interfaces may include qualities and performance
    characteristics.

-   (Optional) Quality-/Performance characteristics of the black box,
    e.g.availability, run time behavior, ….

-   (Optional) directory/file location

-   (Optional) Fulfilled requirements (if you need traceability to
    requirements).

-   (Optional) Open issues/problems/risks

*&lt;Purpose/Responsibility&gt;*

*&lt;Interface(s)&gt;*

*&lt;(Optional) Quality/Performance Characteristics&gt;*

*&lt;(Optional) Directory/File Location&gt;*

*&lt;(Optional) Fulfilled Requirements&gt;*

*&lt;(optional) Open Issues/Problems/Risks&gt;*

### &lt;Name black box 2&gt; 

*&lt;black box template&gt;*

### &lt;Name black box n&gt; 

*&lt;black box template&gt;*

### &lt;Name interface 1&gt; 

…

### &lt;Name interface m&gt; 

Level 2 
-------

Here you can specify the inner structure of (some) building blocks from
level 1 as white boxes.

You have to decide which building blocks of your system are important
enough to justify such a detailed description. Please prefer relevance
over completeness. Specify important, surprising, risky, complex or
volatile building blocks. Leave out normal, simple, boring or
standardized parts of your system

### White Box *&lt;building block 1&gt;* 

…describes the internal structure of *building block 1*.

*&lt;white box template&gt;*

### White Box *&lt;building block 2&gt;* 

*&lt;white box template&gt;*

…

### White Box *&lt;building block m&gt;* 

*&lt;white box template&gt;*

Level 3 
-------

Here you can specify the inner structure of (some) building blocks from
level 2 as white boxes.

When you need more detailed levels of your architecture please copy this
part of arc42 for additional levels.

### White Box &lt;\_building block x.1\_&gt; 

Specifies the internal structure of *building block x.1*.

*&lt;white box template&gt;*

### White Box &lt;\_building block x.2\_&gt; 

*&lt;white box template&gt;*

### White Box &lt;\_building block y.1\_&gt; 

*&lt;white box template&gt;*

Runtime View 
============



**Contents.**

The runtime view describes concrete behavior and interactions of the
system’s building blocks in form of scenarios from the following areas:

-   important use cases or features: how do building blocks execute
    them?

-   interactions at critical external interfaces: how do building blocks
    cooperate with users and neighboring systems?

-   operation and administration: launch, start-up, stop

-   error and exception scenarios

Remark: The main criterion for the choice of possible scenarios
(sequences, workflows) is their **architectural relevance**. It is
**not** important to describe a large number of scenarios. You should
rather document a representative selection.

**Motivation.**

You should understand how (instances of) building blocks of your system
perform their job and communicate at runtime. You will mainly capture
scenarios in your documentation to communicate your architecture to
stakeholders that are less willing or able to read and understand the
static models (building block view, deployment view).

**Form.**

There are many notations for describing scenarios, e.g.

-   numbered list of steps (in natural language)

-   activity diagrams or flow charts

-   sequence diagrams

-   BPMN or EPCs (event process chains)

-   state machines

-   …

&lt;Runtime Scenario 1&gt;
--------------------------

-   *&lt;insert runtime diagram or textual description of the
    scenario&gt;*

-   *&lt;insert description of the notable aspects of the interactions
    between the building block instances depicted in this diagram.&gt;*

&lt;Runtime Scenario 2&gt; 
--------------------------

… {#_}
-

&lt;Runtime Scenario n&gt; 
--------------------------

Deployment View 
===============

**Content.**

The deployment view describes:

1.  the technical infrastructure used to execute your system, with
    infrastructure elements like geographical locations, environments,
    computers, processors, channels and net topologies as well as other
    infrastructure elements and

2.  the mapping of (software) building blocks to that infrastructure
    elements.

Often systems are executed in different environments, e.g. development
environment, test environment, production environment. In such cases you
should document all relevant environments.

Especially document the deployment view when your software is executed
as distributed system with more then one computer, processor, server or
container or when you design and construct your own hardware processors
and chips.

From a software perspective it is sufficient to capture those elements
of the infrastructure that are needed to show the deployment of your
building blocks. Hardware architects can go beyond that and describe the
infrastructure to any level of detail they need to capture.

**Motivation.**

Software does not run without hardware. This underlying infrastructure
can and will influence your system and/or some cross-cutting concepts.
Therefore, you need to know the infrastructure.

Maybe the highest level deployment diagram is already contained in
section 3.2. as technical context with your own infrastructure as ONE
black box. In this section you will zoom into this black box using
additional deployment diagrams:

-   UML offers deployment diagrams to express that view. Use it,
    probably with nested diagrams, when your infrastructure is more
    complex.

-   When your (hardware) stakeholders prefer other kinds of diagrams
    rather than the deployment diagram, let them use any kind that is
    able to show nodes and channels of the infrastructure.

Infrastructure Level 1 
----------------------

Describe (usually in a combination of diagrams, tables, and text):

-   the distribution of your system to multiple locations, environments,
    computers, processors, .. as well as the physical connections
    between them

-   important justification or motivation for this deployment structure

-   Quality and/or performance features of the infrastructure

-   the mapping of software artifacts to elements of the infrastructure

For multiple environments or alternative deployments please copy that
section of arc42 for all relevant environments.

**Environment: KIND (local single-node Kubernetes)**

The deployment artifacts live in `kind-deployment/`. The whole system runs on a
single-node KIND cluster (one Docker container acting as the Kubernetes node),
split into capability namespaces, fronted by an nginx ingress, and reachable
from the developer host through host-port mappings.

```mermaid
flowchart TB
    browser["Web browser<br/>resolves *.localhost &rarr; 127.0.0.1"]
    tools["Management tools<br/>psql &middot; DBeaver &middot; RabbitMQ UI"]

    subgraph host["Developer host &mdash; localhost"]
        h80["TCP :80"]
        h5432["TCP :5432"]
        h5433["TCP :5433"]
        h5672["TCP :5672"]
        h15672["TCP :15672"]
    end

    subgraph node["KIND node &middot; Docker container quizzler-control-plane &middot; single control-plane node"]
        direction TB

        subgraph nsing["namespace: ingress-nginx"]
            ctrl["ingress-nginx-controller<br/>Deployment x1 &middot; hostPort 80/443"]
        end

        subgraph nsq["namespace: quizzler"]
            qui["quizzler-ui<br/>Deployment x2 &middot; nginx :80"]
            qapi["quizzler-api<br/>Deployment x2 &middot; Spring Boot :8080"]
            qdb[("quizzler-db<br/>Deployment x1 &middot; PostgreSQL :5432<br/>PVC quizzler-db-data")]
        end

        subgraph nsp["namespace: payment"]
            pui["payment-ui<br/>Deployment x2 &middot; nginx :80"]
            papi["payment-api<br/>Deployment x2 &middot; Spring Boot :8081"]
            pdb[("payment-db<br/>Deployment x1 &middot; PostgreSQL :5432<br/>PVC payment-db-data")]
        end

        subgraph nsm["namespace: messaging"]
            mq["rabbitmq<br/>Deployment x1 &middot; AMQP :5672 &middot; UI :15672"]
        end
    end

    browser -->|HTTP| h80
    tools --> h5432
    tools --> h5433
    tools --> h5672
    tools --> h15672

    h80 --> ctrl
    ctrl -->|"Host: quizzler.localhost"| qui
    ctrl -->|"Host: api.quizzler.localhost"| qapi
    ctrl -->|"Host: payment.localhost"| pui
    ctrl -->|"Host: api.payment.localhost"| papi

    h5432 -->|"NodePort 30432"| qdb
    h5433 -->|"NodePort 30433"| pdb
    h5672 -->|"NodePort 30000"| mq
    h15672 -->|"NodePort 30001"| mq

    qapi -->|JDBC| qdb
    papi -->|JDBC| pdb
    qapi -->|"POST /payment (create)"| papi
    papi -->|"success webhook (callback)"| qapi

    classDef infra fill:#eef2ff,stroke:#8899cc;
    classDef app fill:#eefaee,stroke:#88aa88;
    class ctrl,qdb,pdb,mq infra;
    class qui,qapi,pui,papi app;
```

Motivation

:   A KIND cluster reproduces a realistic Kubernetes topology (namespaces,
    Deployments, Services, ingress, NodePort) on a single developer machine, so
    rolling updates and the two-replica-per-pod layout can be exercised without
    cloud infrastructure. Capabilities are isolated in their own namespaces
    (`quizzler`, `payment`, `messaging`) to keep ownership and blast radius
    clear. Browser-facing traffic enters through one nginx ingress on port 80
    using host-based rules, while east-west calls use in-cluster service DNS.

Quality and/or Performance Features

:   Every application pod runs **two replicas** (`*-ui`, `*-api`) for rolling,
    zero-downtime updates; the stateful PostgreSQL pods run a **single** replica
    bound to a `ReadWriteOnce` PVC so data survives pod restarts. Host-port
    mappings expose the databases (5432/5433) and RabbitMQ (5672/15672) on
    localhost for management tooling. The deployment is driven by a unique,
    content-distinguishing image tag per `deploy.sh` run, so `kubectl apply`
    rolls pods only when an image actually changed.

Mapping of Building Blocks to Infrastructure

:   | Building block | Namespace | Workload | Replicas | Reached via |
    |----------------|-----------|----------|----------|-------------|
    | quizzler-ui  | quizzler  | Deployment (nginx) | 2 | ingress `quizzler.localhost` |
    | quizzler-api | quizzler  | Deployment (Spring Boot) | 2 | ingress `api.quizzler.localhost` |
    | quizzler-db  | quizzler  | Deployment (PostgreSQL) + PVC | 1 | ClusterIP DNS; host `localhost:5432` (NodePort 30432) |
    | payment-ui   | payment   | Deployment (nginx) | 2 | ingress `payment.localhost` |
    | payment-api  | payment   | Deployment (Spring Boot) | 2 | ingress `api.payment.localhost` |
    | payment-db   | payment   | Deployment (PostgreSQL) + PVC | 1 | ClusterIP DNS; host `localhost:5433` (NodePort 30433) |
    | rabbitmq     | messaging | Deployment | 1 | host `localhost:5672`/`15672` (NodePort 30000/30001) |
    | ingress controller | ingress-nginx | Deployment (hostPort 80/443) | 1 | host `localhost:80` |

    **Note:** RabbitMQ is currently provisioned as infrastructure (deployed and
    host-exposed) but **not yet consumed by the APIs** — there is no AMQP
    dependency, configuration, or client code in `quizzler-api`/`payment-api`
    yet, so no application-to-broker edge is shown. Wiring the services to the
    broker is a planned next step.

Infrastructure Level 2 
----------------------

Here you can include the internal structure of (some) infrastructure
elements from level 1.

Please copy the structure from level 1 for each selected element.

### *&lt;Infrastructure Element 1&gt;* 

*&lt;diagram + explanation&gt;*

### *&lt;Infrastructure Element 2&gt;* 

*&lt;diagram + explanation&gt;*

…

### *&lt;Infrastructure Element n&gt;* 

*&lt;diagram + explanation&gt;*

Cross-cutting Concepts 
======================

**Basic Architecture**

Follows the principle of clean architecture.
Having separate packages for entities representing the core business logic.
Implementing concrete use cases in separate packages.
The direction of dependency is use cases -> entities.

**Domain Model**

***Core domains***

***Domain question-bank*** 
The quiz domain is our code domain. It provides the questions and answers.
It contains following list of entities:
- question (can be a single pick, multiple pick or decision question, has one solution)
- evaluation (evaluates the provided answers of a question)
- solution (provides the solution to the question, solves one question)

***Domain quiz***
The quiz-run domain supports quizz runs by connection quizz, moderator and participants to a run.
It contains following entities:
- quizz (a quizz contains a collection of questions )
- quizzrun (represents a run of a quizz
- moderator
- participant

**Angular design principles**
This section is dedicated to applied design principles for the angular ui.

- questions are presented and submitted as angular forms (reactive forms)
- semantic correctness of selections in questions (e.g. selection of requested number of correct options) are validated by
form validation (e.g.: number of currently selected options exceeds the max number of correct options -> one selection is false for sure and would lead to 0 point when submitted)
- signals are the preferred way of handling state in components; component fields that represent state are declared as `signal`/`computed` rather than plain properties
- conversion from `Observable` to signal via `toSignal` is performed in the component (not in the service); services keep their `Observable<T>` return types, and the component is the layer that subscribes by binding the stream to a signal. This way we can keep the services reusable for consumers that may require observables.

**Java Spring Boot REST API design principles**
This section lists the design principles applied when creating Java Spring Boot REST APIs. They are derived from, and illustrated by, the existing quizzler API (`com.quizzler.api`).

- *Layered architecture with thin controllers*: each request flows through `controller -> service -> repository -> domain`. Controllers only translate between HTTP and method calls and immediately delegate to a service (see `QuestionController`, `QuizAttemptController`, `QuizAttemptPurchaseController`); all business rules, validation and persistence orchestration live in the service layer.
- *DTOs are the wire contract — entities never cross the controller boundary*: controllers accept and return only `*Dto` types, and the service is responsible for mapping domain entities to DTOs (e.g. a private `toDto`) and request DTOs to domain operations. This decouples the API from the persistence model so each can evolve independently. Returning a JPA entity from a controller is not allowed.
- *DTOs expose only what the client needs*: fields that must not leak are deliberately omitted from the DTO. `SinglePickQuestionDto` omits `correctOptionId` so the solution never reaches the client, and it carries a `type` discriminator field for forward-compatibility with future question types.
- *Immutable response DTOs, bindable request DTOs*: response DTOs are immutable (`final` fields, all-args constructor, getters only — see `QuizAttemptDto`, `QuizAttemptPurchaseDto`). Request-body DTOs (`AnswerSubmissionDto`, `QuizAttemptRequestDto`) provide a no-arg constructor plus fields/setters so Jackson can deserialize them.
- *Opaque public identifiers in the API, never internal database ids*: every externally referenced entity carries an internal `@GeneratedValue` `id` used solely for relational mapping, and a separate unique `publicId` (a random `UUID`). URLs, request bodies and response bodies reference `publicId` only; the sequential `id` never crosses the boundary, which prevents resource enumeration.
- *Resource-oriented, hierarchical URLs that express ownership*: nested resources are nested in the path, e.g. `/session/{sessionId}/attempt/{attemptId}/answer` and `/session/{sessionId}/quiz-attempt-purchase`. The base path is declared once per controller with a class-level `@RequestMapping`; individual operations add only their sub-path.
- *Use HTTP verbs and status codes for their defined meaning*: resource creation is `POST` and answers `201 Created` (`@ResponseStatus(HttpStatus.CREATED)`); reads are `GET` returning `200`. Error conditions are signalled with the matching status code, not a `200` carrying an error payload.
- *Errors are raised as `ResponseStatusException` in the service layer*, with the appropriate status and a human-readable message: `404 NOT_FOUND` for a missing or wrong-typed resource, `409 CONFLICT` when the request conflicts with resource state (e.g. a session whose specification has no questions), and `403 FORBIDDEN` for an ownership violation.
- *Cross-resource integrity and authorization are enforced in the service before any mutation*: a child operation verifies its parent relationship rather than trusting client-supplied ids — an attempt must belong to the session in the URL, and a purchase must have been issued for that same session (`QuizAttemptService.createAttempt` rejects a mismatched purchase with `403`).
- *Constructor injection only*: collaborators are `private final` fields wired through a single constructor; field and setter injection are not used. This keeps dependencies explicit and lets services be unit-tested as plain objects (see the **Unit testing** conventions).
- *Transaction boundaries live on the service methods*: mutating use cases are annotated `@Transactional`, read-only queries `@Transactional(readOnly = true)`, and `spring.jpa.open-in-view=false` keeps the persistence context out of the web layer — DTOs are fully populated inside the transaction so no lazy-loading happens during serialization.
- *Cross-cutting web configuration is centralized*: concerns such as CORS are configured once in `WebConfig`, not scattered across controllers.



**Integration of two REST APIs (service-to-service calls)**
This section describes how one of our Spring Boot services consumes another service's REST API. The concept is cross-cutting because every outbound integration in the system follows the same shape. The reference example is the quizzler API calling the payment API to create a payment: `QuizAttemptPurchaseService.initiatePayment` delegates the actual HTTP call to a dedicated `PaymentApiClient`, which talks to `api/payment` (`com.quizzler.payment`).

- *Outbound calls live in a dedicated client adapter, never inline in a service*: every remote API is wrapped in a `*ApiClient` `@Component` (e.g. `PaymentApiClient`) that owns the `RestTemplate` and the remote base URL. The service depends on the client through ordinary constructor injection and stays unaware of HTTP verbs, URLs, JSON and status codes. The integration concern is therefore isolated in one class, and the service remains a pure unit test by mocking the client (`@Mock PaymentApiClient`).
- *The remote base URL is externalized configuration, never hardcoded*: the collaborating API's host is injected with `@Value("${payment.api.base-url}")` and declared in `application.properties` (`payment.api.base-url=http://localhost:8081`). The test profile points the same property at a stub/mock, so no environment assumptions leak into Java code.
- *A single shared `RestTemplate` bean*: the HTTP client is built once in `RestClientConfig` from a `RestTemplateBuilder` and reused by every adapter, giving one place to configure timeouts, interceptors and message converters.
- *The wire format is its own pair of client-side DTOs, decoupled from both domains*: the request and response of the call are dedicated classes owned by the **calling** side (`PaymentCreationRequest`, `PaymentCreationResponse`), independent of the payment service's internal DTOs and of the quizzler domain entities. The response DTO is a *tolerant reader* — annotated `@JsonIgnoreProperties(ignoreUnknown = true)` so it consumes only the fields it needs and remote additions never break deserialization.
- *Remote failures are translated into local HTTP semantics*: a missing or malformed response is converted into a `ResponseStatusException(HttpStatus.BAD_GATEWAY)`, so a downstream outage surfaces to *our* clients as a meaningful `502` instead of a raw exception leaking through the controller.
- *The service orchestrates, the client transports*: the service does the domain work first — load and authorize the purchase, then derive the callback URLs from configured base URLs — and only then hands plain values to the client. Mapping domain values onto the wire request stays on the service/client seam, mirroring the "DTOs are the wire contract" rule used for inbound requests.
- *Integration is bidirectional via callbacks (redirect + webhooks)*: because payment settlement is asynchronous, the two services are both client and server to each other. On the outbound call the quizzler API passes URLs that point back at itself and the UI (`redirectUrl`, `webhookSuccessUrl`, `webhookCancelUrl`); the payment API later calls *back* into the quizzler API's own REST surface (`POST /session/{sessionId}/quiz-attempt-purchase/{purchaseId}/confirmation`) to report the outcome. The callback endpoints are plain controllers that obey every inbound REST principle above.

The `PaymentApiClient` is the reference adapter — it owns the `RestTemplate`, externalizes the base URL, maps to/from the wire DTOs and translates failures:

```java
@Component
public class PaymentApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PaymentApiClient(RestTemplate restTemplate,
                            @Value("${payment.api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public String createPayment(String transactionId,
                                int price,
                                String redirectUrl,
                                String webhookSuccessUrl,
                                String webhookCancelUrl) {
        PaymentCreationRequest request = new PaymentCreationRequest(
                transactionId, price, redirectUrl, webhookSuccessUrl, webhookCancelUrl);
        PaymentCreationResponse response = restTemplate.postForObject(
                baseUrl + "/payment", request, PaymentCreationResponse.class);
        if (response == null || response.getPaymentId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Payment API did not return a payment id");
        }
        return response.getPaymentId();
    }
}
```

The `RestTemplate` is provided once as a shared bean, and the response DTO is a tolerant reader so unknown remote fields are ignored:

```java
@Configuration
public class RestClientConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentCreationResponse {
    private String paymentId;
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
}
```

The service stays free of HTTP details: it does the domain work, builds the callback URLs from configured base URLs, and delegates the transport to the client:

```java
@Transactional(readOnly = true)
public PaymentInitiationDto initiatePayment(String sessionPublicId, String purchaseId) {
    QuizAttemptPurchase purchase = quizAttemptPurchaseRepository.findByPublicId(purchaseId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Purchase " + purchaseId + " not found"));
    if (!purchase.getSession().getPublicId().equals(sessionPublicId)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Purchase " + purchaseId + " does not belong to session " + sessionPublicId);
    }

    String redirectUrl = apiBaseUrl + "/session/" + sessionPublicId
            + "/quiz-attempt-purchase/" + purchase.getPublicId() + "/pymentconfirmation";
    String webhookSuccessUrl = uiBaseUrl + "/quiz-session/" + sessionPublicId
            + "/quiz-attempt-purchase-confirmed/";
    String webhookCancelUrl = uiBaseUrl + "/quiz-session/" + sessionPublicId
            + "/quiz-attempt-purchase-failed/";

    String paymentId = paymentApiClient.createPayment(
            purchase.getPublicId(), PRICE, redirectUrl, webhookSuccessUrl, webhookCancelUrl);
    return new PaymentInitiationDto(paymentId);
}
```



**Asynchronous messaging (event publishing)**
This section describes how a service emits a domain event to the message broker (RabbitMQ) instead of — or, during a migration, in addition to — a synchronous REST call. It is cross-cutting because every outbound event in the system is published the same way: through a generic `RabbitEventPublisher<T>` base class whose destination is declared on the concrete subclass. The reference example is the payment API announcing a confirmed payment so the quizzler API can confirm the matching purchase **without the payment service calling it directly** — the first step in replacing the success-webhook HTTP callback (see *Integration of two REST APIs*) with a message-based integration.

- *All publishing behaviour lives in one generic superclass*: `RabbitEventPublisher<T>` (`com.quizzler.payment.messaging`) is an abstract base parameterised by the event type `T`. It owns the shared `RabbitTemplate` and exposes a single `publish(T event)`; concrete publishers carry no messaging logic of their own, which keeps the broker interaction in exactly one place.
- *The destination is declarative — set via an annotation, not constructor arguments*: a concrete publisher is annotated `@RabbitPublication(exchange = …, routingKey = …)`. The base class reads that annotation once in its constructor (`AnnotationUtils.findAnnotation(getClass(), …)`) and caches the exchange and routing key, so the destination is a fixed property of the publisher *type* rather than a value passed on every send. A publisher that forgets the annotation fails fast at bean creation with an `IllegalStateException` instead of silently sending nowhere.
- *A concrete publisher declares only two things — the event type and the destination*: it `extends RabbitEventPublisher<ConcreteEvent>` and carries the `@RabbitPublication`. `PaymentConfirmationPublisher` is the reference adapter: it binds `PaymentConfirmedEvent` to the payment-events exchange and the `payment.confirmed` routing key and adds nothing else.
- *Publishing is best-effort while the webhook is still authoritative*: during the migration the synchronous success-webhook remains the system of record, so a broker outage must not fail the business operation. `publish` catches `AmqpException`, logs a warning and returns normally instead of propagating — the payment is still confirmed even when the event cannot be sent. This deliberately loosens once the HTTP callback is retired and the message becomes the authoritative integration.
- *Topology names are shared constants, defined once*: the exchange and routing key (and, on the consumer side, the queue) are `public static final String` constants on `RabbitConfig`, referenced both by the `@RabbitPublication` annotation and by the broker-topology beans, so the producer and the declared infrastructure cannot drift apart. `String` constants are used precisely because annotation attributes must be compile-time constants.
- *Events are their own classes, serialized as JSON*: the payload is a dedicated event type (`PaymentConfirmedEvent`) that carries only the correlation data the consumer needs — here the `transactionId`, which is the quizzler purchase reference — independent of the publisher's persistence model, mirroring the "DTOs are the wire contract" rule used for REST. Messages are converted with a `Jackson2JsonMessageConverter` built from the Spring-managed `ObjectMapper`, so JSR-310 types such as `Instant` (de)serialize correctly and the consumer can read the event by field name without a shared library between the two services.

The generic superclass owns the `RabbitTemplate`, derives its destination from the annotation, and performs the best-effort send:

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RabbitPublication {
    String exchange();
    String routingKey();
}

public abstract class RabbitEventPublisher<T> {

    private static final Logger log = LoggerFactory.getLogger(RabbitEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    protected RabbitEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        RabbitPublication publication = AnnotationUtils.findAnnotation(getClass(), RabbitPublication.class);
        if (publication == null) {
            throw new IllegalStateException(
                    getClass().getName() + " must be annotated with @" + RabbitPublication.class.getSimpleName());
        }
        this.exchange = publication.exchange();
        this.routingKey = publication.routingKey();
    }

    public void publish(T event) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
        } catch (AmqpException ex) {
            log.warn("Failed to publish {} to exchange '{}' with routing key '{}': {}",
                    event.getClass().getSimpleName(), exchange, routingKey, ex.getMessage());
        }
    }
}
```

A concrete publisher then reduces to a type binding plus the declarative destination — no messaging code:

```java
@Component
@RabbitPublication(
        exchange = RabbitConfig.PAYMENT_EVENTS_EXCHANGE,
        routingKey = RabbitConfig.PAYMENT_CONFIRMED_ROUTING_KEY)
public class PaymentConfirmationPublisher extends RabbitEventPublisher<PaymentConfirmedEvent> {

    public PaymentConfirmationPublisher(RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate);
    }
}
```

Adding a new outbound event is therefore a three-line affair: define the event class, subclass `RabbitEventPublisher<NewEvent>`, and annotate it with the destination — the base class supplies the transport, the error handling and the destination wiring.



**Unit testing**
This section captures the conventions used for unit tests across the system. They apply to all back-end JUnit tests; the same spirit applies to Angular Jest tests where the tooling allows.

- *Object graph comparison for assertions*: a test asserts the **whole expected result object** against the actual result in a single comparison, rather than asserting field-by-field. On the back-end this is done with AssertJ's `assertThat(actual).usingRecursiveComparison().isEqualTo(expected)`. The expected object is built explicitly in the test body so the reader can see the full shape that is being verified. This keeps tests resilient to internal refactorings (no churn when fields are added) while still failing loudly when the contract changes.
- *Readable test method naming*: test methods follow the schema `methodUnderTest_when_condition_then_outcome` (or the shorter `methodUnderTest_condition_outcome`) with snake_case separators, e.g. `getSinglePickQuestion_which_exists_is_returned`, `getSinglePickQuestion_when_not_exists_throws`. The intent is that the method name reads as a sentence describing the scenario, so a test report acts as a behavioural specification of the system.
- *One scenario per test*: each test covers a single behavioural scenario (one happy path, one sad path, …). All assertions for that scenario live in the same test method — typically a single object-graph comparison plus, where applicable, an exception assertion. Granular per-field tests and defensive/meta tests (e.g. reflection-based DTO surface checks) are avoided.
- *Pure unit tests for service-layer code*: services are exercised without a Spring context — `@ExtendWith(MockitoExtension.class)` plus `@Mock` for collaborators and `@InjectMocks` for the unit under test. JPA-generated `id` fields are set with `ReflectionTestUtils.setField` rather than introducing test-only setters on production entities.

The `createSession_assigns_a_question_as_current_without_neighbours` test from `QuizSessionServiceTest` shows the pattern:

```java
@ExtendWith(MockitoExtension.class)
class QuizSessionServiceTest {

    @Mock
    private QuizSessionRepository quizSessionRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuizSessionService quizSessionService;

    @Test
    void createSession_assigns_a_question_as_current_without_neighbours() {
        SinglePickQuestion question = new SinglePickQuestion("Title", "Text");
        ReflectionTestUtils.setField(question, "id", 42L);
        QuizSessionDto expected = new QuizSessionDto(SESSION_PUBLIC_ID, 42L, 0L, 0L);
        when(questionRepository.findAll()).thenReturn(List.of(question));
        when(quizSessionRepository.save(any(QuizSession.class))).thenAnswer(call -> call.getArgument(0));

        QuizSessionDto dto = quizSessionService.createSession();

        assertThat(dto.getPublicId()).isNotBlank();
        assertThat(dto).usingRecursiveComparison().ignoringFields("publicId").isEqualTo(expected);
    }
}
```

- *Component tests for the back-end through its HTTP surface*: a controller and the slice of the system behind it (service, repository, persistence) are tested together as one component, exercised only through the REST API — never by calling Java methods directly. The full application is started with `@SpringBootTest(webEnvironment = RANDOM_PORT)` and `@AutoConfigureWebTestClient`; an in-memory H2 database (`src/test/resources/application.properties`) replaces PostgreSQL so the test owns its data. Requests are issued with an injected `WebTestClient`, fixtures are seeded and cleared per test via the real repositories in a `@BeforeEach`, and the response body is asserted as a DTO with the object-graph comparison above. `QuizSessionControllerTest` is the reference example: it drives `POST /session` and `GET /session/{publicId}` end to end, seeds exactly one question so the randomly assigned `currentQuestion` is deterministic, and excludes the server-generated `publicId` from the recursive comparison (`ignoringFields("publicId")`) while still asserting it is non-blank.

The `createSession_assigns_the_only_question_as_current` test from `QuizSessionControllerTest` shows the pattern:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class QuizSessionControllerTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizSessionRepository quizSessionRepository;

    private Long seededQuestionId;

    @BeforeEach
    void seedTestData() {
        quizSessionRepository.deleteAll();
        questionRepository.deleteAll();

        SinglePickQuestion question = new SinglePickQuestion(QUESTION_TITLE, QUESTION_TEXT);
        seededQuestionId = questionRepository.save(question).getId();
    }

    @Test
    public void createSession_assigns_the_only_question_as_current(@Autowired WebTestClient webTestClient) {
        QuizSessionDto expected = new QuizSessionDto(null, seededQuestionId, 0L, 0L);

        webTestClient.post().uri(SESSION).exchange()
                .expectStatus().isCreated()
                .expectBody(QuizSessionDto.class)
                .value(dto -> {
                    assertThat(dto.getPublicId()).isNotBlank();
                    assertThat(dto).usingRecursiveComparison()
                            .ignoringFields("publicId")
                            .isEqualTo(expected);
                });
    }
}
```

**Content.**

This section describes overall, principal regulations and solution ideas
that are relevant in multiple parts (= cross-cutting) of your system.
Such concepts are often related to multiple building blocks. They can
include many different topics, such as

-   domain models

-   architecture patterns or design patterns

-   rules for using specific technology

-   principal, often technical decisions of overall decisions

-   implementation rules

**Motivation.**

Concepts form the basis for *conceptual integrity* (consistency,
homogeneity) of the architecture. Thus, they are an important
contribution to achieve inner qualities of your system.

Some of these concepts cannot be assigned to individual building blocks
(e.g. security or safety). This is the place in the template that we
provided for a cohesive specification of such concepts.

**Form.**

The form can be varied:

-   concept papers with any kind of structure

-   cross-cutting model excerpts or scenarios using notations of the
    architecture views

-   sample implementations, especially for technical concepts

-   reference to typical usage of standard frameworks (e.g. using
    Hibernate for object/relational mapping)

**Structure.**

A potential (but not mandatory) structure for this section could be:

-   Domain concepts

-   User Experience concepts (UX)

-   Safety and security concepts

-   Architecture and design patterns

-   "Under-the-hood"

-   development concepts

-   operational concepts

Note: it might be difficult to assign individual concepts to one
specific topic on this list.

![Possible topics for crosscutting concepts](images/08-Crosscutting-Concepts-Structure-EN.png)

*&lt;Concept 1&gt;* 
-------------------

*&lt;explanation&gt;*

*&lt;Concept 2&gt;* 
-------------------

*&lt;explanation&gt;*

…

*&lt;Concept n&gt;* 
-------------------

*&lt;explanation&gt;*

Design Decisions 
================

- **Use gRPC** for realizing RPCs from the client to the backend. As upcoming versions may need bidirectional streaming capabilities (e.g.: collecting currently selected answers from all quizzers (answer collection)) and the interface should be ideomatic, gRPC was favoured over REST. 

-- **Use capabilities of angular forms for semantic validation** for semantic validation of questions (e.g.: if all requested options are selected) angular forms will be used. The special logic behind single pick, pick or decision questions regarding the number of options that can be selected is covered by form validation. Alternatively this validation logic could be seen as part of the model classes but this would lead unnecessary complexity and circumvent angular and html form caopabilities. 

- **Mutationless (append-only) persistence for the payment domain** (`api/payment`, package `com.quizzler.payment`). The payment service never updates or deletes rows; a payment's life cycle is recorded as a sequence of immutable inserts spread over separate entities, tables, repositories and controllers: `Payment` (the creation), `PaymentConfirmation` and `PaymentCancellation`. Each record carries its own opaque `publicId` and an insertion timestamp (`created_at`), and every business column is mapped `updatable = false` so the schema enforces immutability at the persistence layer. A payment has **no mutable `status` column** — its current state is *derived* from which records exist (no terminal record → pending; a confirmation → confirmed; a cancellation → cancelled). Transitions are modelled as `POST` operations that *create* the corresponding sub-resource (`POST /payment/{paymentId}/confirmation`, `POST /payment/{paymentId}/cancellation`, both answering `201 Created`); the service rejects a transition with `409 Conflict` when a terminal record already exists, so confirmation and cancellation remain mutually exclusive. The confirmation/cancellation association to `Payment` is `@OneToOne` with a **unique `payment_id`** column: a duplicate transition of the *same* kind is therefore rejected by the database at insert time (`saveAndFlush` → `DataIntegrityViolationException`, translated to `409`), avoiding a redundant pre-insert existence query. The cross-kind mutual exclusion (cannot confirm an already-cancelled payment, and vice versa) spans two tables and so remains a single explicit existence query.
  - *Rationale*: yields a complete, tamper-evident audit trail (every state change is a timestamped, retained fact rather than an overwrite); makes concurrent transitions easy to reason about and detect (the unique constraint is the authoritative guard against races); and aligns with the REST design principle of treating state changes as the creation of immutable sub-resources.
  - *Trade-off*: reads that need the current status must aggregate across the three tables instead of reading a single column, and the cross-kind exclusion is enforced in application code rather than by a single-row state machine.

- **Monetary amounts are modelled as integer values in cents.** Every price/amount in the system — across persistence, API DTOs and the wire format, and the frontend transport — is a whole number representing the smallest currency unit (euro cents); e.g. a price of €2.00 is the integer `200`. This avoids the binary floating-point rounding errors inherent in `float`/`double` for money and keeps amounts exact, comparable and safe to sum across the API boundary. Conversion to a human-readable major-unit representation (e.g. `2.00 €`) happens only at the presentation layer.

Quality Requirements 
====================

**Content.**

This section contains all quality requirements as quality tree with
scenarios. The most important ones have already been described in
section 1.2. (quality goals)

Here you can also capture quality requirements with lesser priority,
which will not create high risks when they are not fully achieved.

**Motivation.**

Since quality requirements will have a lot of influence on architectural
decisions you should know for every stakeholder what is really important
to them, concrete and measurable.

Quality Tree 
------------

**Content.**

The quality tree (as defined in ATAM – Architecture Tradeoff Analysis
Method) with quality/evaluation scenarios as leafs.

**Motivation.**

The tree structure with priorities provides an overview for a sometimes
large number of quality requirements.

**Form.**

The quality tree is a high-level overview of the quality goals and
requirements:

-   tree-like refinement of the term "quality". Use "quality" or
    "usefulness" as a root

-   a mind map with quality categories as main branches

In any case the tree should include links to the scenarios of the
following section.

Quality Scenarios 
-----------------

**Contents.**

Concretization of (sometimes vague or implicit) quality requirements
using (quality) scenarios.

These scenarios describe what should happen when a stimulus arrives at
the system.

For architects, two kinds of scenarios are important:

-   Usage scenarios (also called application scenarios or use case
    scenarios) describe the system’s runtime reaction to a certain
    stimulus. This also includes scenarios that describe the system’s
    efficiency or performance. Example: The system reacts to a user’s
    request within one second.

-   Change scenarios describe a modification of the system or of its
    immediate environment. Example: Additional functionality is
    implemented or requirements for a quality attribute change.

**Motivation.**

Scenarios make quality requirements concrete and allow to more easily
measure or decide whether they are fulfilled.

Especially when you want to assess your architecture using methods like
ATAM you need to describe your quality goals (from section 1.2) more
precisely down to a level of scenarios that can be discussed and
evaluated.

**Form.**

Tabular or free form text.

Risks and Technical Debts 
=========================

**Technical Debts**

- Foreign Key not generated on quiz_attempt to session. 
- QuizAttemptController -> rename publicId to sessionId for better readability
- Foreign Key not generated on answer to attempt
- Transactional annotation on QuizAttemptService are only necessary for test run against h2
- Need to delete all QuizAttemptsPurchases from h2 database in setup test data of QuizSessionControllerTests (maybe this is not necessary)
- (SEcurity) make the TransactionId unique in the Payment COlumn
- The hard coded urls to payment and quizzler api should be provided by configuration parameters (this will be changed anyway when it comes to KIND deployment).

Glossary 
========

**Contents.**

The most important domain and technical terms that your stakeholders use
when discussing the system.

You can also see the glossary as source for translations if you work in
multi-language teams.

**Motivation.**

You should clearly define your terms, so that all stakeholders

-   have an identical understanding of these terms

-   do not use synonyms and homonyms

**Form.**

A table with columns &lt;Term&gt; and &lt;Definition&gt;.

Potentially more columns in case you need translations.

| Term                              | Definition                        |
| --------------------------------- | --------------------------------- |
| Participant                           | A person that participates to a quizz in answering questions              |
| Quizz                             | A sequence of questions           |
| Quizzrun                          | A run through the questions of a quizz                  |



