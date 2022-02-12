# CRM API
This API was built using Java - Spring Boot. To use you would need SendGrid and Cloudinary accounts.

## Configuration

### System Envs
Please set the following environments in your environment:

| Name of Env | Description |
|------------ | ----------- | 
| SENDGRID_API_KEY | Used to authenticate the app with Sendgrid |
| CLOUDINARY_CLOUD_NAME | Name of the "cloud" space allocated in Cloudinary |
| CLOUDINARY_API_KEY | Used to authenticate the app with Cloudinary |
| CLOUDINARY_API_SECRET | Used to authenticate the app with Cloudinary |

### Applicationn Properties

Also, you can customize the some application updating the properties in the `application.properties` file.

## Using the API

### Registering a user

* POST http://localhost:8080/register
```json
{
    "username": "username",
    "password": "password",
    "matchingPassword": "password"
}
```
You will have to register to the application in order to be able to manage Customer information. In order to so, please provider a user and password to be used in the following requests.
If you want to register as an <b>Admin</b> user, include a `roles` field in the previous JSON and set it to `ROLE_ADMIN`.

<mark>All the following endpoints use HTTP Basic Authentication.</mark>

### Getting list of customers

* GET http://localhost:8080/customers

Gets a list of all customers in the database.

### Get customer

* GET http://localhost:8080/customers/{customer_id}

Get all the info of a single customer

### Creating a customer

* POST http://localhost:8080/customers

```json
{
  "name": "First Cust",
  "surname": "Surname",
  "photo_url": null
}
```

Creating a customer in the database.

### Creating many customers with CSV file

* POST http://localhost:8080/customers/upload
- Body mode "file"

Uses a CSV file with the headers: `name`, `surname`, `photo_url`. To create many customer in one request.

### Updating a customer

* PUT http://localhost:8080/customers/{customer_id}

```json
{
  "name": "First Cust",
  "surname": "Surname",
  "photo_url": null
}
```
Updates customer information

### Uploading a photo

* POST http://localhost:8080/customers/{customer_id}/photo
- Body mode "file"

Receives an image and uploads it to Cloudinary

### Deleting a photo

* DELETE http://localhost:8080/customers/{customer_id}

Delete a customer from the database.

## User management
<mark>The following endpoints need a user with the `ADMIN` role</mark>

### Create a user

* POST http://localhost:8080/register

```json
{
  "username": "username",
  "first_name": "FirstName",
  "last_name": "lastName",
  "roles": "ROLE_USER"
}
```
Creates a user in the system. Which can later be used to authenticate using http Basic Auth.

### Getting list of users

* GET http://localhost:8080/users

Gets a list of users

### Get user

* GET http://localhost:8080/users/{user_id}

Gets all the info for a user

### Update a user

* PUT http://localhost:8080/users/{user_id}

```json
{
    "id": 7,
    "username": "username",
    "firstName": "User",
    "lastName": "Name",
    "roles": "ROLE_USER"
}
```
Update informtion for a user (except the id)

### Delete a user

* DELETE http://localhost:8080/users/{user_id}

Deletes a user from the system.

