console.log("This is script file");

//Function for "Sidebar" operation.
//function toggleSidebar() {
const toggleSidebar=() => {

  if($(".sidebar").is(":visible")) {

    //Close the sidebar.
    $(".sidebar").css("display", "none");
    $(".content").css("margin-left", "0%");

  } else {

    //Show the sidebar.
    $(".sidebar").css("display", "block");
    $(".content").css("margin-left", "20%");
  }

};

const search = () => {

//    console.log(query);
    let query = $("#search-input").val();
    console.log(query);

    if (query == '') {

        $(".search-result").hide();

    } else {

        //Search.
        console.log(query);

        //Sending request to server.
        let url = `http://localhost:8080/search/${query}`;

        fetch(url).then((response) => {

            return response.json(); //Converting into "JSON".

        })
        .then((data) => {

            //console.log(data);

            let text = `<div class='list-group'>`;

            data.forEach((contact) => {

                text += `<a href='/user/${contact.id}/contact-detail' class='list-group-item list-group-action'> ${contact.name}</a>`;
            });

            text += `</div>`;

            $(".search-result").html(text);
            $(".search-result").show();
        });
    }
};

//First request - to server to create order.
const paymentStart = () => {

    console.log("payment started...");

    let amount = $("#payment_field").val();

    console.log(amount);

    if (amount == '' || amount == null) {

//    alert("Amount is required !!");
        swal("Failed", "Amount is required !!", "error");

        return;
    }

    //Code.
    //We will use "ajax" to send request to server to create order - jquery.
    $.ajax(
        {
            url: '/user/create_order',
            data: JSON.stringify({ amount: amount, info: 'order_request' }),
            contentType: 'application/json',
            type: 'POST',
            dataType: 'json',

            success: function (response) {

                //Invoked when success.
                console.log(response)

                if (response.status == 'created') {

                    //Open payment form.
                    let options = {
                        key: 'rzp_test_m36y7H3aZmfb94',
                        amount: response.amount,
                        currency: 'INR',
                        name: 'Smart Contact Manager',
                        description: 'Donation',
                        image: 'http://www.scmmarkets.com/wp-content/uploads/2017/08/scmlogo_nowords_Dark.png',
                        order_id: response.id,

                        handler: function (response) {

                            console.log(response.razorpay_payment_id);
                            console.log(response.razorpay_order_id);
                            console.log(response.razorpay_signature);

                            console.log("Payment Successful !!");
//                        alert("Congrats!! Payment Successful !!");

                            updatePaymentOnServer(response.razorpay_payment_id, response.razorpay_order_id, "Paid");

 //                           swal("Good Job!", "Congrats!! Payment Successful !!", "success");
                        },
                        prefill: {         //We can keep the data on "Server".
                            "name": "",
                            "email": "",
                            "contact": ""
                        },
                        notes: {
                            address: "Smart Code Manager"
                        },
                        theme: {
                            color: "#3399cc"
                        }
                    };

                    let rzp = new Razorpay(options);

                    //When payment will be failed.
                    rzp.on('payment.failed', function (response) {
                        console.log(response.error.code);
                        console.log(response.error.description);
                        console.log(response.error.source);
                        console.log(response.error.step);
                        console.log(response.error.reason);
                        console.log(response.error.metadata.order_id);
                        console.log(response.error.metadata.payment_id);

    //                    alert("Oops payment failed !!");
                        swal("Failed", "Oops payment failed !!", "error");
                    });

                    rzp.open();
                }

            },
            error: function (error) {

                //Invoked when error.
                console.log(error)
                alert("Something went wrong !!")
            }
        }
    )

};

//For updating status of payment on database.
const updatePaymentOnServer = (payment_id, order_id, status) => {
    $.ajax({
            url: '/user/update_order',
            data: JSON.stringify({
                payment_id: payment_id,
                order_id: order_id,
                status: status
            }),
            contentType: 'application/json',
            type: 'POST',
            dataType: 'json',

            success: function (response) {
                swal("Good Job!", "Congrats!! Payment Successful !!", "success");
            },
            error: function (error) {
                swal("Failed !!", "Your Payment Successful, but we didn't get on server, we will contact you as soon as possible", "error");
            }
        })
};






