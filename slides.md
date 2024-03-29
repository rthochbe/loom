---
theme: seriph
background: images/stephane-gagnon-NLgqFA9Lg_E-unsplash.jpg
class: text-center
highlighter: shiki
lineNumbers: false
info: |
  ## Slidev Starter Template
  Presentation slides for developers.

  Learn more at [Sli.dev](https://sli.dev)
drawings:
  persist: false
defaults:
  foo: true
transition: slide-left
title: Ask me about Loom
mdc: true
---

# Ask me about Loom...

Virtual Threads in Java 21

<div class="pt-12">
  <span @click="$slidev.nav.next" class="px-2 py-1 rounded cursor-pointer" hover="bg-white bg-opacity-10">
    Press Space for next page <carbon:arrow-right class="inline"/>
  </span>
</div>

<div class="abs-br m-6 flex gap-2">
  <button>Foto von <a href="https://unsplash.com/de/@metriics?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash">Stephane Gagnon</a> auf <a href="https://unsplash.com/de/fotos/orangeblau-weisses-textil-NLgqFA9Lg_E?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash">Unsplash</a></button>
  <button @click="$slidev.nav.openInEditor()" title="Open in Editor" class="text-xl slidev-icon-btn opacity-50 !border-none !hover:text-white">
    <carbon:edit />
  </button>
  <a href="https://github.com/rthochbe/loom" target="_blank" alt="GitHub" title="Open in GitHub"
    class="text-xl slidev-icon-btn opacity-50 !border-none !hover:text-white">
    <carbon-logo-github />
  </a>
</div>

<!--
The last comment block of each slide will be treated as slide notes. It will be visible and editable in Presenter Mode along with the slide. [Read more in the docs](https://sli.dev/guide/syntax.html#notes)
-->

---

```yaml
layout: cover
background: images/loom-menu.png
class: 'text-white'
```

<div class="abs-br m-6 flex gap-2">
  <button>from <a href=https://en.wikipedia.org/wiki/The_Secret_of_Monkey_Island>The secret of monkey island</a></button>
  <a href="https://github.com/rthochbe/loom" target="_blank" alt="GitHub" title="Open in GitHub"
    class="text-xl slidev-icon-btn opacity-50 !border-none !hover:text-white">
    <carbon-logo-github />
  </a>
</div>

---

```yaml
layout: cover
background: images/loom.jpeg
```

<div class="abs-br m-6 flex gap-2">
  <button>see <a href=https://en.wikipedia.org/wiki/Loom_(video_game)>LOOM</a></button>
  <a href="https://github.com/slidevjs/slidev" target="_blank" alt="GitHub" title="Open in GitHub"
    class="text-xl slidev-icon-btn opacity-50 !border-none !hover:text-white">
    <carbon-logo-github />
  </a>
</div>

---

# ... another story - Project Loom 
__Goal__: Implement a lightweight and scalable threading model (so called fibres) on top of the existing platform threads to let people keep using the simple models and tooling they have been using for years but gaining a lot of performance. 

### Quite a journey until now
- started in 2017
- 2019 - new Socket API ships with JDK 13
- 2020 - reimplemented Datagram Socket API ships with JDK 15
- 2021 - Draft for virtual threads and structured concurrency
- 2022 - Preview feature in JDK 19
- October 2023 - Release virtual threads with JDK 21

<div class="abs-br m-6 flex gap-2">
  <a href="https://github.com/slidevjs/slidev" target="_blank" alt="GitHub" title="Open in GitHub"
    class="text-xl slidev-icon-btn opacity-50 !border-none !hover:text-white">
    <carbon-logo-github />
  </a>
</div>

---

# But lets have a look at the problem first...
There are to different types of threads in a Java program: 
- User Threads  
  Started from the program code and running in the the background. 
- Kernel Threads  
  A native Thread managed by the Operating System. It will preempt a thread in favor of other running threads to share cpu time.

Right now if we create a user thread we will als create a native kernel thread and using OS resources. 

---

# Threads are heavyweight resources
- Consuming round about 1MB each outside of the heap.
- Thread context switching costs cpu time. (unloading and loading registers)    
- Therefore the rule of thumb: Only use few threads (One per 100Mhz CPU), Context Switching and memory could kill you otherwise. 

Standard Spring Boot Application 
```
robert@MacBook-Pro-von-Robert images % jstack 74533
2024-02-11 20:56:22
Full thread dump OpenJDK 64-Bit Server VM (21.0.2+13-58 mixed mode, emulated-client, sharing):

Threads class SMR info:
_java_thread_list=0x0000600001139540, length=32, elements={
0x000000012e00b000, 0x000000012e015a00, 0x000000012e016200, 0x000000012d817c00,
0x000000012d818400, 0x000000012d818c00, 0x000000012e017a00, 0x000000012e118800,
0x000000012e119000, 0x000000012d944400, 0x000000011e086000, 0x000000012d96da00,
0x000000011e072a00, 0x000000011e0bbe00, 0x000000012d96fc00, 0x000000011e0be600,
0x000000011e0bdc00, 0x000000011e0c1000, 0x000000011d934000, 0x000000011d930c00,
0x000000011d931400, 0x000000012e1df000, 0x000000012e1bfe00, 0x000000012e1a4e00,
0x000000012e1c2c00, 0x000000012e1ee800, 0x000000012e1c3400, 0x000000012f008800,
0x000000010e80a600, 0x000000011e008200, 0x000000011e032600, 0x000000012e1eaa00
}


```
---

# Project Loom Basics
- Virtal Threads (Fibres) run on top of a normal user thread
- Scheduling and Continuation is done by the java runtime (as opposed to kernel threads)
- Program execution could be suspended at any time and continued on a different user thread. 
- The stack is allocated on the heap and therefore also subject to garbage collection. 

---

# Structured Concurrency (preview feature)

- Makes it easier to split work into simple tasks
- It consists mainly of methods around a so called `StructuredTaskScope`
- Supports Shutdown Policies
- There will be additional tooling for debugging  (Threaddumps via jcmd showing the structurte of such tasks)

```java

    Callable<String> task1 = ...
    Callable<Integer> task2 = ...

    try (var scope = new StructuredTaskScope<Object>()) {

        Subtask<String> subtask1 = scope.fork(task1);
        Subtask<Integer> subtask2 = scope.fork(task2);

        scope.join();

        ... process results/exceptions ...

    } // close
```

---

# Virtual Threads - Best Practices

- keep the work done in a virtual thread small (large call stacks will cost memory and garbage collection time)
- avoid pinning of threads (eg. through calling synchronized)
- avoid splitting up cpu intensive tasks (more virtual threads could do harm there too)
- you got most out of the new concept for typical tasks with i/o wait time.  

---

# Do we still need Project Reactor? 

### Pro virtual threads
- may be used with the common programing model
- existing programs may get some boost since the blocking APIs dont do that much harm. 
- easier to use and understand
  

### Pro reactive
- more wholesome aproach (enforcing cleaner tasks)
- The whole (endless) streaming is not covered 
- still the more performant way of doing things (execution performance)

---

# Spring Boot & Virtual Threads

- with Version 3.2 Spring Boot supports natively virtual threads
- they could simply be enabled by  
  ```
  spring.threads.virtual.enabled=true
  ```

