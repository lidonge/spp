# S++进阶建模

在S++建模初步中采用了一种直观的自然建模方法，自然建模总是由场景产生契约的格式，就好像几个人临时凑在一起在没有充分准备的前提下临时起草签署了一份合约。实际上，现实社会中大部分的契约是有标准格式的，不需要临时拟定，采用预定义的标准格式契约进行建模的方式就是预定义契约建模。采用预定义契约建模，可以更好的抽象模型。

## 预定义契约建模

还是沿用订单这个场景，通过预定义契约，可以用下面的代码来描述buyer签约。

```
Identify Contract buyer{
	Person;
	Address;
	datetime joindate;
	string nickname;
	string loginname;
	string paypassword;
};
identify behavior joinMember;

service JoinMember 
	from Person, Address, buyer
	by joinMember
	using buyer;
//or simplify to
service JoinMember 
	by joinMember
	using buyer;
```

与自然签约的区别是，由于事先拟定了契约的内容，所以行为就不再包含任何签约的内容信息了，那就意味着这个行为比原来要抽象，可以用于类似的签约过程比如seller的签约。

```
Identify Contract seller{
	Person;
	Address;
	datetime opendate;
	string shopName;
	string shopDesc;
};
service openShop 
	by joinMember
	using seller;
```

这个定义可以理解为，系统有两种成员，一种叫seller，一种叫buyer。在实现的形式上，自然建模是场景创建了契约，预定义建模是契约作为参与者记录了流水；在实体流水中，自然建模行为与契约一一对应，所以记录行为还是契约名称都可以，而预定义建模中必须要记录契约名称，因为抽象的行为丢失了一部分信息。

## 身份契约多态性

上面的定义方式还可以进一步抽象。

```
identify behavior join;
Identify abstract Contract member{
	Person;
	Address;
	datetime joindate;
	string name;
}
service register
	by join
	using member;

Identify Contract seller act member{
	opendate override joindate;
	shopname override name;

	string shopDesc;
	
	castrule: default;
};

Identify Contract buyer act member{
	nickname override name;

	string loginname;
	string paypassword;
	
    castrule: loginname != null;
};
```

根据多态性的定义，相同的行为，不同的参与者参与，导致不同的业务。在这个例子中，相同的行为是join，参与者是buyer契约时就是注册一个买家，如果参与者是卖家契约则就是一个开店的业务。多态性简化了业务模型，使得多个业务场景合并为同一个业务场景。

S++多态性的实现不同于面向对象，S++是在服务端实现的，当场景组合去调用量子服务member*join时，量子服务容器根据castrule来动态决定调用的是seller还是buyer。

这个例子中为了方便说明，所以使用了用户定义的castrule，实际上用户定义的castrule非常的麻烦，而且会导致实现过于复杂。采用一种类似于面向对象的纯粹技术的castrule是一种更恰当的选择，比如直接用契约的名称作为系统内置的castrule。

## 通用身份契约

S++并不建议使用通用身份契约，虽然这个模型可以囊括所有身份类的契约。

```
identify Contract GeneralID{
	Object[];
	Participant[];
	string type;
	string desc;
};

identify behavior register;

service registerAll
	by register
	using GeneralID;
```

这样的模型隐藏了所有的业务信息，使得描述变得完全不可读。模型并不是越简单越好，一个好的模型首先要恰当合适的表达业务要素，然后才是精简。

## 预定义可执行契约

可执行契约细分有两种：

1. 立即完成契约

   这种契约在签署的同时就被执行并到达终止态。比如现金存款这个业务，业务完成后就获得一张回单并完成契约。

2. 延迟完成契约

   契约不是立刻被执行并终结，后续可能会有多次状态变迁，比如订单这个例子。

预定义的方式并不适用于多状态契约，因为每一次状态变更都可能产生不同格式的契约，所以预先定义好每种契约格式并不是很方便，而且不利于业务的扩展。但是，预定义的方式是适合立即完成契约的。

**利用多态性提高可执行契约的扩展性**

考虑刷卡消费场景：

```
identify abstract contract financialAccount{
	Person;
	Store;
	int unit = 100;
	string currency = "usd";
}
behavior pay;

executable contract bill{
	financialAccount;
	int amount;
}
service payByCard
	by pay
	using bill;

identify contract creditCard act financialAccount{
	string cardNo;
	limit: 
		Store.amount >= -10000;
}
quantom creditCard*pay{
	//System should check the limit automaticlly
	Stote.amount.decrease(pay.amount);
}

identify contract depositCard act financialAccount{
	string cardNo;
	limit: 
		Store.amount >= 0;
}
quantom depositCard*pay{
	Stote.amount.decrease(pay.amount);
}
```

这个模型的实现中，由于pay是抽象的，所以需要在运行时刻由系统给pay填入预定义契约中的参数bill.amount。

**组合的场景**

在上面的例子中，如果每次消费都会产生一定的积分，可以如下定义：

<img src="http://latex.codecogs.com/gif.latex?B=B_{payByCard}+p_{points}*b_{earnPoints}" align=center />

在S++中，不推荐使用嵌套的场景描述，因为这样会为了获得更简单的模型而增加耦合性。应如下定义：

```
identify contract pointsAccount{
	Person;
	Store;
    ...
}
behavior earnPoints;

executable contract pointsBill{
	pointsAccount;
	int amount;
};

quantom pointsAccount*earnPoints{
	Stote.amount.increase(earnPoints.amount);
}

executable contract mixBill{
	financialAccount;
	pointsBill;
}
service payByCardEarnPoints
        from financialAccount
        by pay
        using bill;
	plus
        from pointsAccount
        by earnPoints
        using pointsBill
	using mixBill;
```

## S++多态性建模的一些原则

S++多态性与面向对象的多态性存在着很大的差异性，因此在利用多态性建模方面也有很大的差别：

- S++是面向业务分析师的建模工具，所以要尽可能的暴露业务活动

  面向对象会尽可能的隐藏业务细节，也就是追求更好的封装性。比如上面的例子中，如果用面向对象的方法设计，产生积分的过程就会被隐藏起来，从外部看payByCard不会有任何变化，而在内部则会从pay函数中调用earnPoints函数。好处就是，当我们需要仅对信用卡业务产生积分的时候，可以仅修改信用卡的pay函数就可以了，对外部就可以隐藏这段业务活动，使得对使用这个服务的程序开发人员更加透明，降低对技术人员的业务要求。

  而对于S++而言，业务分析师建立的模型并不需要程序员去实现，所以为了更清楚明确的表达业务，就要放弃封装性，并反过来尽可能多的暴露业务活动。

- S++多态性不能改变场景，既不能增加参与者，也不能增加行为

  S++量子服务的调度发生在组合场景层，而多态性的实现是在量子服务层，所以多态性仅能实现参与者动态的选择，并不能实现业务场景的变化。比如上面的例子中，如果想增加消费积分这样的业务，必须在场景中明确的表达出来，不能通过多态性动态的实现。下面这个模型无法在S++中实现：

  ```
  service payByCard
  	from financialAccount
  	by pay
  	using bill;
  
  identify contract creditCard act financialAccount{...}
  quantom creditCard*pay{
  	Stote.amount.decrease(pay.amount);
  	//You can not call this quantom service, or access the object, because quantom is 
  	//zero coupling
  	pointsAccount*earnPoints; //Error access
  }
  ```

  