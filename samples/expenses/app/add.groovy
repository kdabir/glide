new Transaction(
	description: params.description, 
	amount: (params.amount as Double), 
	user: users.currentUser, 
	date:new Date()
).save()

redirect "/"