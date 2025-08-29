#### employees API
```
GET       /employees    #obtain employee list
GET       /employees/1  # obtain a certain specific employee
GET       /employees?page=1&pageSize=5  #Page query, page equals 1, pageSize equals 5
GET       /employees?gender=male   #screen all male employees
POST      /employees    # add an employee
PUT       /employees/1  #update an employee
DELETE    /employees/1  #delete an employee
```

#### Company API
```
GET       /companies    #obtain company list
GET       /companies/1  #obtain a certain specific company
GET       /companies/1/employees  # obtain list of all employee under a certain 
specific company
GET       /companies?page=1&pageSize=5  #Page query, if page equals 1, pageSize equals 5, it will return the data in company list from index 0 to index 4.
POST      /companies    #add a company
PUT       /companies/1  #update a company basic information
DELETE    /companies/1  # delete a company
```