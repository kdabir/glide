final per_page = 3
final total_trans = Transaction.count()

html.html{
	body{
        h1 "Expense Tracker"
        hr()

        h3 "Add a new expense"
        form(method: "post", action: "/add") {
            div {
                input(type: "text", name: "amount", value: "", placeholder:"Amount")
            }
            div {
                input(type: "text", name: "description", value: "", placeholder:"Desciption")
            }
            div {
                input(type: "submit", value: "Submit")
            }
        }

        h3 "Expenses added"
        div "displaying ${(total_trans>per_page)?per_page:total_trans} of ${total_trans}"

		table {
			tr {
				th "Date"
				th "Amount"
				th "Desciption"
			}
			Transaction.findAll({sort desc by date limit per_page}).each { transaction->
				tr {
					td transaction?.date?.format('MM/dd/yy')
					td transaction?.amount
					td transaction?.description
				}
			}		
		}
	}
}