package DuongVanBao.event.service.impl;

import DuongVanBao.event.model.UserDetailsImpl;
import DuongVanBao.event.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final NguoiDungRepository nguoiDungRepository;

    @Override
    public UserDetails loadUserByUsername(String maNguoiDung) throws UsernameNotFoundException {
        return nguoiDungRepository.findByMaNguoiDung(maNguoiDung)
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
    }
}
