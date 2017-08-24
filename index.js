import React, { Component, PropTypes } from 'react';
import { requireNativeComponent,DeviceEventEmitter,NativeModules} from 'react-native';
var LSScanapi = requireNativeComponent('LSScan', LSScanApi);

export default class LSScanApi extends Component {
    static propTypes = {
        /**
         *
         * 定义组件需要传到原生端的属性
         * 使用React.PropTypes来进行校验
         */
        descText:PropTypes.string,
        onchanges:PropTypes.func
    };
    _onchange=(event) =>{
        if(this.props.onChange && event.nativeEvent){
            this.props.onChange(event.nativeEvent);
        }
    }
    render() {

        return (
            <LSScanapi {...this.props}
                onChange={this._onchange}
            />
        );
    }
}