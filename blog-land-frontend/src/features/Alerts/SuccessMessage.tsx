import Swal from 'sweetalert2';

export const ShowSuccessSwal = (title: string, message: string) => {
  Swal.fire({
    icon: 'success',
    title,
    text: message,
    timer: 2500,
    showConfirmButton: false,
    position: 'top-end',
    background: '#fff',
    iconColor: '#4caf50',
    customClass: {
      popup: 'swal-popup-small',
      title: 'swal-title-small',
    },
  });
};
