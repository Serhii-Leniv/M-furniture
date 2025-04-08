package m.furniture.M_f.Service;

import m.furniture.M_f.Entity.Call;
import m.furniture.M_f.Repository.CallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CallService {
    @Autowired
    private CallRepository callRepository;

    public void savePhone(Call call) {
        callRepository.save(call);
    }

}
