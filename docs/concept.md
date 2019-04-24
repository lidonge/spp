# Concepts and Applications

The concept of S++ was proposed in 2015 and officially published in InfoQ in 2016. S++ redefines service-oriented, clarifies the fuzzy parts of traditional SOA, determines the method of identifying the minimum granularity of services, and gives the mathematical model of the service.

# Conceptual explanation

Computer software is a mapping to the real world. Object-oriented objects simulate the world by mapping entities into classes and objects, and modeling the business through interaction between objects. S++ looks at the problem from another perspective. It maps business activities into behaviors to simulate reality, and completes business scenario modeling by participating in business activities by different participants. These are two very different modeling methods. The former completes the static model first and then interacts with the model. The latter first builds the model of the active scene and then joins the actors (participants) to complete the business scenario. S++ is based on a series of inferences based on observed facts (physical called measurement results or empirical experience). First, several concepts should be clarified:

**Actions** The concept comes from object-oriented thinking. All object entities can be packaged with their own actions to form a complete object. The granularity of the action of the object can be arbitrarily divided and there can be no clear business goals and results.

**behavior** is an activity with inherent logic and purpose. Behavior is generally done by one or more objects (a set of actions), and the behavior must produce business results based on business goals.

**Participant** is the object entity involved in the behavior. In the S++ method, the behavior exists as the subject, and the objects (including people) become the object participants.

How to distinguish between the two concepts of behavior and action? You can use the example of eating to illustrate that eating has a clear internal logic and purpose (such as relieving hunger), so eating is an act; while mouth opening and chewing, because it may not have a clear purpose (can eat You can also talk or bite things, so you can only be seen as the action of the entity. In the act of eating, at least two objects, the object and the food, exist. These two objects are called participants of the eating behavior.

## Redefining services in SOA

Based on the above concepts, S++ redefines the service: ** Service is the sum of a set of business activities**. The following model is an abstract definition of a service:
$$
S=\sum_{i=0}^n B_i =\sum_{i=0}^n p_i*b_i
$$
The capitalized ***B*** represents the **business activity**, with clear business objectives and intrinsic business logic, and will eventually produce certain business results. Business activity is the product of the participant and the behavior. The physical meaning is the behavior with the participants' participation. The result is the business result with the business goal, which is represented by the following model:
$$
B=p*b
$$
Where B represents the goal and outcome of the business activity, p represents the business participant (participant), and b represents the behavior (behavior). Business behavior B is a business goal achieved by the business entity p participating in the behavior b. Since neither p nor b can complete the business goal independently, the product is used to indicate that it is indispensable (when any of p and b is zero, That is, when there is no action or no entity, the business result B as a product is zero).

- **Distribution law**

$$
B=p*(b_1+b_2)=p*b_1+p*b_2=B_1+B_2
$$

$$
B=(p_1+p_2)*b=p_1*b+p_2*b=B_1+B_2
$$

- **Exchange Law**
  $$
  B=B_1+B_2=B_2+B_1
  $$

The law of distribution and the law of exchange are the conclusions of real world observation and incomplete induction. The law of distribution is easy to understand because it conforms to the basic method of divide and conquer; but the exchange law tells us that business activities can be in no order, which is obviously contrary to Intuition. The reason for the inconsistency is that we usually observe business activities in an object-oriented way. The following S++ method is used to analyze the business activities of relieving hunger (eat), and the exchange law can be explained by the independence of behavior.

** Behavior is independent**

For the business activity of releasing hunger*B*, there are two participants (*p1*) and food (*p2*), and eating this behavior*b*, based on the previous definition we can conclude:
$$
B=(p_1+p_2)*b=p_1*b+p_2*b=B_1+B_2
$$
The literal meaning of this formula is to relieve hunger = people eat + food to eat, if it is ridiculous to analyze according to the object-oriented method, how to eat food? In fact, the actual physical meaning of this formula is: relieve hunger = people's blood sugar rise + food consumption. S++ can directly refer to the physical nature of business activities. Once the business activities are broken down into such granularity, we will find:

- There is no inevitable causal relationship between the rise in blood sugar and the consumption of food. The two can happen independently.
- If we only focus on the business results, the order of the two is not important. (In fact, for automated services, we really only focus on business results, usually the service is treated as a black box)

The independence of behavior explains the source of the exchange law. At the same time, in the process of analyzing the independence of behavior, it can be found that the business activities are not freely subdivided. After reaching a certain granularity and continuing to split, the business activities that meet the business objectives cannot be obtained. That is to say, business activities are not continuously separable. This property happens to correspond to the quantum nature of energy, so S++ defines that the behavior of the smallest granularity is quantum behavior**, and the corresponding business activity of the smallest granularity is called quantum service**.

## Automated Process and Manual Process

The exchange law is the mapping of ** quantum superposition ** in the virtual world. The challenge of superposition is almost an instinct of human beings. To realize that business processes can be executed out of order, it is necessary to study the essential difference between automated processes and manual processes. From the above analysis, we can see that business activities are composed of quantum services, and the service itself can be regarded as a black box for the result. The establishment of the exchange law is actually the embodiment of quantum. A business activity can be seen as an uncertain quantum cloud before the outside world observes its result B. The state inside the cloud is completely random, and only the moment of observation collapses into the result B. That is to say, when the outside world observes the result B (whether it is observed by a person or observed by a computer), the observer actually participates in the entire business process, and the process is divided into two stages: pre-observation and post-observation. Then the exchange law loses its effect under the interference of the observer.

From the above analysis, it can be concluded that the exchange law only works in the automated process. The key to distinguish between the automated process and the manual process is not whether someone participates, but whether the intermediate result of the business activity needs to be observed during the execution of the process. In other words, any process that needs to read and check intermediate results is a manual process. The exchange law is a very important basic law of S++. To make the business activities conform to the exchange law, it is necessary to completely eliminate the observation of the intermediate results of the business process. In fact, the real-world sequential processes are mostly unavoidable observations due to technical reasons. But these observations may not be necessary in the virtual computer world.

Give an example to illustrate how to eliminate observations in software, such as going to the gym to play the business. This business activity can be simplified into two quantum services: "contribution + play". In reality, some implementations must be paid first. This is because some business activities cannot be reversed. If you play first and then pay, you may not be able to pay. Received such a loss of money. Such a situation can be understood as a technical means to limit business activities. If there are certain technical means to reverse the business activities to the initial state, then there is no need to consider whether the two business activities of payment and playing are in order. Fortunately, in the virtual world, almost all behaviors are reversible. For a computer, all business activities are nothing more than changes in the data on the storage medium, so almost all observations and detections of intermediate results need to be eliminated. .

**Note: ** Quantum entanglement** is temporarily not implemented in S++. When participants participate in a business scenario, all participants are entangled from a quantum perspective. If entanglement can be achieved, then all participants in the scene will change at the same moment, and all participants will change simultaneously, which is impossible to achieve with current technical means. In S++ implementation, the process of quantum entanglement in the physical world can be simulated by parallel execution of all quantum services in the scene.

- # S++ Conceptual Inference and Application

  Understanding the basic concepts of S++, you can draw several important inferences:

  - Service and technology can be separated in service
  - Automation services can be executed out of order
  - Service has time and space invariance
  - Zero coupling between quantum services
  - Service has polymorphism

  These inferences have great significance for the implementation of S++, and provide a feasible solution to the problems that traditional SOA and microservices cannot solve.

  ## Business and technology separation

  In real life, almost all behaviors require the participation of tools, and we usually use technology to optimize our activities. For example, we will use tableware to eat, and the tableware is a technical participant. With the tableware, the process of eating is more sanitary and labor-saving. For example, when we drink hot soup, we will use tableware to stir. Stirring will let the soup cool down quickly to improve the efficiency of our meal. Stirring is a technical behavior. By observing we found that technical behavior and participants have some characteristics:

  - Technical participants and behavior do not change the purpose of business activities. For example, the business activity of relieving hunger is to use chopsticks, or use a knife and fork or even directly to grasp it. It has no effect on the purpose of relieving hunger. All technical means and tools are designed to better help humans achieve their business goals, but technology and tools are not the goals themselves.
  - Technology participants and behavior also do not change the outcome of business activities. Any business goal that can be achieved must have one or more optional tools to help people achieve the same business results, and more and more tools can be selected as technology advances. The use of any tableware in the business activities to relieve hunger, the results are the same, and will not eat enough because of the use of chopsticks.
  - Technical activities follow the law of distribution, which means that technical activities can be performed separately and independently.

  When the service incorporates technical behavior and participants, the mathematical model of the service can be expressed as:
  $$
  S=B+T=\sum_{i=0}^n B_i+\sum_{i=0}^nT_i
  $$
  $$
  S=\sum_{i=0}^n p_i*b_i+(\sum_{i=0}^n t_i* B_i + \sum_{i=0}^n p_i*t_i+\sum_{i=0}^n t_i*t_i)
  $$

  $$
  S=\sum_{i=0}^n p_i*b_i +0
  $$

  Where Ti represents the item of technology (behavior or participant) in the product, and since these items have no effect on the final result of the business, the value of these items is zero. The separation of technology and business shows that in a business system, the technical part can be completely independent of development and maintenance, and does not need to be mixed with business code. However, technical activities cannot comply with the exchange law (because the results of technical activities are usually observed), such as the encryption and decryption of messages, must be performed before or after the execution of the business, the determination of permissions must be run before the business is executed, and so on. The separation of technology and business provides great benefits for the development and operation of business systems:

  - ** Built (module) is the income **: The modeling and implementation of business activities can completely get rid of the technical constraints. The traditional system development model, Requirements->Analysis->Design->Encoding->Test->Online, has become two independent and independent processes: the process of modeling the business experts and the process of the technicians meeting the technical requirements.
  - **Business Modeling Zero Coding**: For services that conform to automated processes, the process of modeling no longer focuses on any technical implementation, nor does it need to focus on any branching logic, just define participants and behaviors. And make a simple list. Therefore, the need for coding does not exist.
  - ** Consistency of service connotation and diversity of service extensions**: The business part B of the service reflects the connotation of the business, and the technical part T reflects the extension of the service. The diversity of business is reflected in the rich changes in T, such as the dazzling diversity of payment services. As long as the technology changes a little bit, it may lead to earth-shaking changes in the entire service, but no matter how the service changes in form, its business connotation is stable, which brings huge benefits to the business system based on S++ development. . In the business modeling process of the service, if you only need to pay attention to the connotation of the service, it is a lot simpler for the modeler. Similarly, for the technical staff, there is no need to understand the business rules, only need to focus on the promotion. The technical capabilities of the system can be.

## Out of order execution / parallel operation

The biggest benefit based on the commutative law is parallel computing. Automated services that enable parallel operations at the quantum service level. An unsolvable problem with the microservices architecture is that as the granularity of services decreases, the combined performance of services drops dramatically. Assuming that the time Ts necessary to invoke a service includes: the communication time Tc, the time Td of the database transaction, and the time T0~Tn of the steps required to complete the service, the mathematical model can be used to express the time consumed by the service calls of different architectural forms.

- For traditional high-gather-coupled SOA services, the steps required for business activities are generally done in a database transaction, so the time consumed by the call is expressed as follows:

$$
T_s=T_c+T_d+\sum_{i=0}^nT_i
$$

- For microservices, since the service granularity is much smaller than SOA, assuming that in extreme cases all the steps in the SOA are split into separate calls, then:

$$
T_s=\sum_{i=0}^n(T_c+T_d+T_i)
$$

- For S++, the benefits of parallel computing are:

$$
T_s=T_c+T_d+Max(T_i|_{i=0}^n)
$$

It can be seen from the above three mathematical models that if the SOA service is used as the benchmark, the performance of the microservice will consume n-1 communication and database transactions with the decrease of the granularity, and S++ will run with the decrease of the granularity. The speed will increase.

For microservices, due to the gradual reduction of service granularity, any microservice can not provide independent support for the complete business scenario. Therefore, the combination of services cannot be avoided. If you do not use S++ for modeling, The service simply cannot withstand the performance problems caused by reduced granularity.

- ## Service time and space invariance / service deversion

  Designing a business service with the minds of a technician, intuitively the version of the service must exist. However, from the perspective of separation of business and technology, no matter how rich the service is, the business content of the stripped technology part has almost no change. S++ defines this feature of service as **space-time invariance**. The time-space invariance of services is consistent with the observations of the objective world. For example, the business activities of repairing cars have been around since ancient times, and no matter how advanced the times are, the goals and results of this business are: bad cars go in good cars.

  In fact, the consequences of applying the versioning characteristics of an object intuitively in the process of building a service model are fatal, especially for combining business requirements. If service A is called by a composite service S, then when service A needs to maintain two versions, service S should theoretically maintain both versions. So this seemingly simple problem encountered a huge challenge after the service S combined multiple services (such as A, B, C), if it happens that ABC needs to maintain two versions, the service S is to maintain 2x2x2=8 Versions to deal with different versions of the combination? Obviously this is impossible, so the version of S++ for the service states:

  - The meaning of the service is not allowed to have a version
  - Any modification to the meaning of the service will inevitably result in new services
  - The version of the service is only available in the extended definition of the current service.
  - The combination of services can only call the connotation of the service, and does not allow direct invocation of specific extensions (the implementation of the service)

- ## Zero coupling between quantum services

  If a quantum service A needs to call another service B, then by definition:
  $$
  A=p_0*b_0 + B= p_0*b_0 + \sum_{i=1}^nB_i=\sum_{i=0}^nB_i
  $$
  It can be seen that A is not a quantum service, so there is no calling relationship between quantum services, that is, there is no coupling. Zero coupling description of quantum services:

  - Business activities must be realized by a combination of quantum services
  - Any mutually coupled business system can be further split into two parts: quantum service and business combination

  A serious problem with the traditional microservice architecture is that as the service granularity is refined, the complexity of the service system increases exponentially. The mutual invocation of services leads to a sharp increase in the number of transactions as the number of services increases, which brings a lot of problems:

  - The maintainability of the system is greatly reduced: any service modification will inevitably lead to associated changes, and the number of services is so large.
  - The complexity of the transaction link is uncontrollable, resulting in a serious reliance on the monitoring system.
  - The complexity of the monitoring system has risen sharply, and the demand for resources has even surpassed that of the business system itself. It is like a group of people working on the scene.

  In this case, in order to simplify the transaction link and system complexity, S++ states:

  - Any service cannot contain both the specific business itself and calls to other services.
  - Quantum services can only be called by pure composite services
  - Composite services cannot be called from each other, and composite services can only be modeled by listing participants and behaviors.

## Service polymorphism

In the object-oriented method, the object is the business subject, the type of the object cannot be enumerated, and the instance of the object is inexhaustible. The complexity of system modeling lies in describing the complex object relationship model. For S++, business behavior is the main body, and the behavior pattern is much less than the object type, and it can be enumerated almost. How does S++ use extremely limited behavior to build endless business services? Simply put, the participants determine the business goals and outcomes of the service. For example, for the business behavior of eating, when the participants are diners, the business goal is to relieve hunger; if the participants are guests of the business banquet, then the business goal becomes social; even if the participants are eating contests Athletes, then the business goal becomes a medal.

**The same business behavior, different business goals and results due to different participants, this is the polymorphism of the service**, polymorphism causes a lot of business services in S++ is very similar.

The polymorphism of the service can improve the maintainability of the business process and eliminate the business branches in the traditional business process. In the traditional business process, the service type needs to be judged according to the content of the business field, and the subsequent service call is decided; in S++ The judgment and scheduling of the business branch is automatically executed by the system according to the participants, and different business service implementations are automatically invoked according to different participants. For example, for the payment service of a bank, in the traditional business process, the payment category field is determined, and the telephone fee, water fee, etc. are called according to the content of the field, so if the payment category is to be added, the business process needs to be modified. Undoubtedly reduce the maintainability of the system; in the business process of S++, there is no need to make similar business branch judgments. In the modeling process, only the abstract payment service is called, and the abstract participants in the operation process are replaced by the actual participants. The system automatically completes the business branch function.

The most important difference between S++ service polymorphism and object-oriented polymorphism is that object-oriented polymorphism must be implemented on the client side, and S++ polymorphism can be implemented on the server side. This difference is mainly reflected in the system's coupling degree. Object-oriented to achieve polymorphism, the client must couple all known implementation classes, and at the very least interface level coupling (such as remote objects); and S++ The client can not know exactly how many specific services are implemented. The polymorphism of the service is determined by the server according to the received message. There is no coupling except the definition of the abstract service.

- ## Classification of services

  Services in S++ are divided into two categories, business and technical, with subdivisions under each category.
  
  ### Technology category
  
  By definition, all behaviors and participants that do not affect business goals and outcomes are technical. Therefore, technical services are easily distinguished.
  
  - Verification service
  
    The verification class service is mainly used for data inspection, and is generally used for checking the input information of the service to prevent the dirty data from polluting the service system.
  
  - Conversion service
  
    The conversion class service is mainly used to convert input data of different formats into a format specified by the service system, or reverse conversion.
  
  - Security service
  
    Security services are mainly used for encryption and decryption of sensitive data, tampering of messages, system authentication, permission control, etc. to ensure transaction security.
  
  - Error handling service
  
    Error handling is the use of error interceptors to intercept business and technical errors that occur during service execution, thereby separating error handling from business processes and avoiding interference with services.
  
  ### Business class
  
  Business services are mainly divided into five categories according to behavior patterns:
  
  - Entity maintenance class
  
    S++ and object-oriented have similarities in the maintenance of entities, and there are also big differences. The biggest difference is that the S++ entity class only includes the natural classification and attributes of the entity. For example, the entity of human beings can generate a large number of classifications from different dimensions in object-oriented, such as classification by occupation, classification by age, classification by position, etc.; but in S++, only one class of people is classified by nature. All human entities are classified as natural persons. Human social attributes are expressed through the results of contracting behaviors. Natural entities become contracts by signing up. For example, natural persons sign up with companies to become employees. Usually most services are directed at participants rather than natural entities.
  
  - Contracting class
  
    Contracting is the most common behavior in social activities. In general, contracts are divided into identity contracts and executable contracts. The identity-based contract can be signed between natural entities, or the natural entity can be mixed with the participants. The result of the identity-based contract is to generate new types of participants, in order to meet certain business scenarios. Executable contracts must be signed between participants, and natural entities must attach social attributes to participate in signing an executable contract. An executable contract usually has multiple execution states and results, such as an order being an executable contract, an order can be modified, paid, cancelled, dispatched, closed, evaluated, and so on.
  
  - Contract execution class
  
    The contract execution class service is used for state transitions of executable contracts and for changes in contract participant properties.
  
  - Query class
  
    Query class services are used to query entities, participants, and contract execution results.
  
  - Calculation class
  
    Computational services should strictly belong to other types of business services. For example, computing rates are part of the charging service, and charging must be performed after the rate is calculated to obtain the exact rate. The purpose of separating computing services from business services is to improve the reusability of the computing process, so computing services are more like technical services. In process processing, the calculation class is also similar to the technology class, and the business calculation work needs to be completed according to the participants before the automatic process is initiated.

## Other

### Service security

Since the quantum services in S++ do not have any coupling calls to each other, all calls come from the combined process of business scenarios, so S++ is easier to obtain at a lower cost than traditional SOA and microservices. For quantum services, just a simple IP whitelist check can guarantee the legitimacy of access, because only a limited number of process control nodes can access quantum services. As long as traditional security measures are applied at the entrance to the combined process, the security of the entire system can be ensured.

### Data Storage Method

S++ supports the most thorough distributed storage (of course, you can also use a centralized storage structure), and the data storage layer designed for the characteristics of quantum services can give greater play to the advantages of S++. For a more detailed description, please refer to [["S++ Distributed Database Requirements and Reference Design"]](./db.md) .

Compared with object-oriented modeling, S++ modeling is more conducive to distributed storage of data. In the S++ model, the relationship between objects is greatly weakened, and the types of data tables are few. Basically, there are only entity tables, identity contract tables, executable contract tables, and various types of business flow tables. Therefore, it is very challenging to implement complex relational applications for business intelligence under the S++ distributed data architecture. Of course, S++ can also be built on a single relational database. Such an architecture can satisfy complex relational operations, but a single database cannot satisfy high-concurrency business scenarios. A compromise option is to use space for time and use a data processing system parallel to the business system to complete real-time data storage for business intelligence.

- Quantum services are single-table operations. Due to the independence of quantum services, each quantum service only needs to access a single data table.
- There is no need to establish associations between data tables. The data storage structure with no inter-table relationship is conducive to the operation of sub-tables, and can implement a more flexible dynamic parting strategy.
- S++ distributed data storage does not support association queries at the data layer. Association queries can only be implemented at the service scene layer.

### Recommended layered structure

Depending on the granularity of the service, S++ recommends a distributed four-layer logic architecture. The granularity is sequentially reduced for the interactive service layer, the process service layer, the scene service layer, and the quantum service layer.

See [[S++ Reference Logic Architecture]](./layers.md)  for details.



