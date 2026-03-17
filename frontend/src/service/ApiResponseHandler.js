export async function responseHandler(request) {
  if (!(request instanceof Promise)) return;

  return request
    .then((res) => {
      return {
        status: res.status,
        data: res.data,
        success: true,
        error: "",
      };
    })
    .catch((error) => {
      // this is true when the request gets to the server
      // and there is some error on the server
      if (error.response) {
        return {
          success: false,
          data: null,
          status: error.response.status,
          error:
            (error.response?.data?.substr
              ? error.response.data.substr(6)
              : error.response?.data?.message) || error.message,
        };
      }
      // this is true when the request cant get to the server
      // eg. connection error
      if (error.request) {
        return {
          success: false,
          data: null,
          status: error.code,
          error: error.message,
        };
      }
    });
}
