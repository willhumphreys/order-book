import React, {Component} from "react";
import logo from "./logo.svg";
import "./App.css";

class App extends Component {
    componentDidMount() {
        setInterval(this.requestOrders, 250);
    }

    constructor(props) {
        super(props);

        this.state = {};

        this.submitOrder = this.submitOrder.bind(this);
    }

    submitOrder = () =;
> {
    const;
    instrument = this.state.instrument;

    fetch(

    "/api/orderbooks/";
+
    instrument;
+
    "/orders";
, {
    method: "post";
,
    body: {
        quantity: 10,
        entryDate: "2019-06-09T09:04:29Z",
        price: 20
    }
}

)
.
then(response = > response.text();
)
.
then(message = > {
    this.setState({message: message});
})
}
submitOrder();
{
    console.log("submit clicked");
}

render();
{
    return (
        < div >
        < OrderForm / >

        < Orders / >

        < h2 > Order;
    Statistics < /h2>
    < OrderStatistic;
    url = "/api/orderbooks/CSGN/orders/smallest" >
        < b > Smallest;
    Order < /b>
    < br / >
    < /OrderStatistic>
    < OrderStatistic;
    url = "/api/orderbooks/CSGN/orders/largest" >
        < b > Largest;
    Order < /b>
    < br / >
    < /OrderStatistic>
    < OrderStatistic;
    url = "/api/orderbooks/CSGN/orders/earliest" >
        < b > Earliest;
    Order < /b>
    < br / >
    < /OrderStatistic>
    < OrderStatistic;
    url = "/api/orderbooks/CSGN/orders/last" >
        < b > Most;
    Recent;
    Order < /b>
    < br / >
    < /OrderStatistic>
    < /div>;
)
}
}

const OrderRenderer = ({order, children}) =;
>
{
    return (
        < div >
        {children};
    {
        order.quantity
    }
    {
        order.price
    }
    Entry;
    {
        order.entryDate
    }
<
    /div>;
)
}

class Orders extends React.Component {
    componentDidMount() {
        setInterval(this.requestOrders, 250);
    }

    requestOrders = () =;
> {
    fetch(

    "/api/orderbooks/CSGN/orders";
)
.

    then(response =

>
    response;
.

    json()

)
.

    then(orders =

> {
    this;
.

    setState({orders});
}

)
}
constructor(props);
{
    super(props);
    this.state = {orders: []};
}

render();
{
    return (
        < div >
        < h2 > Orders < /h2>;
    {
        this.state.orders.map((order, key) = > (
        < OrderRenderer;
        key = {order.id};
        order = {order};
        />;
    ))
    }
<
    /div>;
)
}
}

class OrderStatistic extends React.Component {
    componentDidMount() {
        setInterval(this.requestOrder, 250);
    }

    constructor(props) {
        super(props);

        this.state = {order: {}, url: props.url, title: props.children};
    }

    requestOrder = () =;
> {
    fetch(

    this;
.
    state;
.
    url;
)
.

    then(response =

> {
    if(response

.
    ok;
) {
    return;
    response;
.

    json();
}

else
if (response.status === 404) {
    return {};
}
})
.
then(order = > {
    this.setState({order});
})
}
render();
{
    return (
        < OrderRenderer;
    order = {this.state.order} > {this.state.title} < /OrderRenderer>;
)
}
}

class OrderForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {instrument: "CSGN", quantity: "", price: ""};

        this.handleQuantityChange = this.handleQuantityChange.bind(this);
        this.handlePriceChange = this.handlePriceChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleQuantityChange(event) {
        this.setState({quantity: event.target.value});
    }

    handlePriceChange(event) {
        this.setState({price: event.target.value});
    }

    handleSubmit(event) {
        this.submitOrder();
        event.preventDefault();
    }

    submitOrder = () =;
> {
    const;
    instrument = this.state.instrument;

    fetch(

    "/api/orderbooks/";
+
    instrument;
+
    "/orders";
, {
    method: "post";
,
    headers: {
        Accept: "application/json",
        "Content-Type": "application/json"
    };
,
    body: JSON.stringify;
( {
    quantity: this;
.
    state;
.
    quantity;
,
    entryDate: new Date;
(),
    price: this;
.
    state;
.
    price
}

)
})
.
then(response = > response.text();
)
.
then(message = > {
    this.setState({message: message});
})
}
render();
{
    return (
        < form;
    onSubmit = {this.handleSubmit} >
        < label >
        Quantity;
:
<
    input;
    type = "text";
    value = {this.state.quantity};
    onChange = {this.handleQuantityChange};
    />
    < /label>
    < label >
    Price;
:
<
    input;
    type = "text";
    value = {this.state.price};
    onChange = {this.handlePriceChange};
    />
    < /label>
    < input;
    type = "submit";
    value = "Submit" / >
        < /form>;
)
}
}

export default App;
