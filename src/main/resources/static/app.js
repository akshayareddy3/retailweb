let cart = {};

document.addEventListener("DOMContentLoaded", function () {
    const increaseButtons = document.querySelectorAll(".increase-btn");
    const decreaseButtons = document.querySelectorAll(".decrease-btn");

    increaseButtons.forEach(button => {
        button.addEventListener("click", function () {
            const name = this.getAttribute("data-name");
            const price = parseFloat(this.getAttribute("data-price"));

            if (!cart[name]) {
                cart[name] = { price: price, quantity: 0 };
            }

            cart[name].quantity += 1;
            updateQtyDisplay(name);
            renderCart();
        });
    });

    decreaseButtons.forEach(button => {
        button.addEventListener("click", function () {
            const name = this.getAttribute("data-name");

            if (cart[name] && cart[name].quantity > 0) {
                cart[name].quantity -= 1;

                if (cart[name].quantity === 0) {
                    delete cart[name];
                }
            }

            updateQtyDisplay(name);
            renderCart();
        });
    });
});

function updateQtyDisplay(name) {
    const safeId = "qty-" + name.replace(/\s+/g, "-");
    const qtySpan = document.getElementById(safeId);
    if (qtySpan) {
        qtySpan.textContent = cart[name] ? cart[name].quantity : 0;
    }
}

function renderCart() {
    const cartItems = document.getElementById("cartItems");
    const cartTotal = document.getElementById("cartTotal");

    if (!cartItems || !cartTotal) return;

    cartItems.innerHTML = "";
    let total = 0;

    for (let name in cart) {
        const item = cart[name];
        const subtotal = item.price * item.quantity;

        const li = document.createElement("li");
        li.textContent = name + " x " + item.quantity + " = ₹ " + subtotal;
        cartItems.appendChild(li);

        total += subtotal;
    }

    cartTotal.textContent = total;
}

function placeOrder() {
    const keys = Object.keys(cart);

    if (keys.length === 0) {
        alert("Cart is empty");
        return;
    }

    let itemsText = [];
    let total = 0;

    keys.forEach(name => {
        const item = cart[name];
        itemsText.push(name + " x " + item.quantity);
        total += item.price * item.quantity;
    });

    const orderData = {
        customerName: "Demo User",
        items: itemsText.join(", "),
        totalAmount: total,
        paymentMethod: "UPI",
        status: "CONFIRMED"
    };

    fetch("/api/orders", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(orderData)
    })
    .then(response => response.json())
    .then(data => {
        console.log("Order saved:", data);
        cart = {};
        window.location.href = "/user";
    })
    .catch(error => {
        console.error("Order failed:", error);
        alert("Order failed");
    });
}