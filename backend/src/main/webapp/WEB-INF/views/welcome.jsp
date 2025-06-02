<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Spring MVC Sample Application</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .container {
            background: white;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
            text-align: center;
            max-width: 600px;
            margin: 20px;
        }
        h1 {
            color: #333;
            margin-bottom: 20px;
            font-size: 2.5em;
        }
        p {
            color: #666;
            font-size: 1.2em;
            line-height: 1.6;
            margin-bottom: 30px;
        }
        .status {
            background: #4CAF50;
            color: white;
            padding: 10px 20px;
            border-radius: 5px;
            display: inline-block;
            margin-bottom: 20px;
            font-weight: bold;
        }
        .api-info {
            background: #f5f5f5;
            padding: 20px;
            border-radius: 5px;
            margin-top: 20px;
        }
        .api-link {
            color: #667eea;
            text-decoration: none;
            font-weight: bold;
        }
        .api-link:hover {
            text-decoration: underline;
        }
        .timestamp {
            color: #999;
            font-size: 0.9em;
            margin-top: 20px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>${message}</h1>
        <div class="status">✓ Application Running</div>
        <p>${description}</p>
        
        <div class="api-info">
            <h3>API Endpoint Available:</h3>
            <p>Test the REST API: <a href="/api/hello" class="api-link">/api/hello</a></p>
            <p>This endpoint returns JSON data with application status and timestamp.</p>
        </div>
        
        <div class="timestamp">
            Page loaded at: <%= new java.util.Date() %>
        </div>
    </div>
</body>
</html>
