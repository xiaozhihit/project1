import axios from "axios";
import {ElMessage} from "element-plus";
const defaultFailure=(message)=>ElMessage.warning(message)
const defaultError=()=>ElMessage.error("发生未知错误,请联系管理员!")

async function get(url ,success,failure=defaultFailure,error=defaultError){
    await axios.get(url,{
        //由于还是使用基于session的校验,携带cookie
        withCredentials:true
    }).then(({data})=>{
        if(data.success)
            success(data.message,data.status)
        else
            failure(data.message,data.status)
        return data.message;
    }).catch(error)
}
async function post(url, data, success, failure = defaultFailure, error = defaultError) {
    await axios.post(url, data, {
            // headers: {
            //     'Content-Type': 'application/x-www-form-urlencoded',
            // },
            withCredentials: true,
        })
        .then(({ data }) => {
            if (data.success)
                success(data.message, data.status);
             else
                failure(data.message, data.status);
            return data.message;
        })
        .catch(error);
}
async function postForm(url, data, success, failure = defaultFailure, error = defaultError) {
    await axios.post(url, data, {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        withCredentials: true,
    })
        .then(({ data }) => {
            if (data.success)
                success(data.message, data.status);
            else
                failure(data.message, data.status);
            return data.message;
        })
        .catch(error);
}
async function postFile(url, data, success, failure = defaultFailure, error = defaultError) {
    await axios.post(url, data, {
        // headers: {
        //     'Content-Type': 'multipart/form-data'
        // },
        withCredentials: true,
    })
        .then(({ data }) => {
            if (data.success)
                success(data.message, data.status);
            else
                failure(data.message, data.status);
            return data.message;
        })
        .catch(error);
}
async function put(url, data, success, failure = defaultFailure, error = defaultError) {
    await axios.put(url, data, {
        headers: {
            'Content-Type': 'application/json'
        },
        withCredentials: true,
    })
        .then(({ data }) => {
            if (data.success)
                success(data.message, data.status);
            else
                failure(data.message, data.status);
            return data.message;
        })
        .catch(error);
}



export { get,post,postForm,postFile,put}