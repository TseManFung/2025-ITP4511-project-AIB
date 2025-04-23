<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error Occurred</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card border-danger">
                    <div class="card-header bg-danger text-white">
                        <h3>Error Occurred</h3>
                    </div>
                    <div class="card-body">
                        <p class="text-danger">
                            <strong>Error Message:</strong> 
                            <%= exception != null ? exception.getMessage() : "Unknown error occurred." %>
                        </p>
                        <p>
                            <strong>Exception Type:</strong> 
                            <%= exception != null ? exception.getClass().getName() : "N/A" %>
                        </p>
                        <p>
                            <strong>Stack Trace:</strong>
                        </p>
                        <pre class="bg-light p-3">
                            <%= exception != null ? exception.toString() : "No stack trace available." %>
                        </pre>
                        <a href="<%= request.getContextPath() %>/" class="btn btn-primary mt-3">Go to Homepage</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>